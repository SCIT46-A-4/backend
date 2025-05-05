$(document).ready(function () {
  console.log('✅ ドキュメント読み込み完了');

  // 元のフォームデータと画像パスを保存するための変数
  let originalFormData = {};
  let originalImagePath = '';

  // FilePond プラグイン登録
  FilePond.registerPlugin(
    FilePondPluginImagePreview,
    FilePondPluginFileValidateType,
    FilePondPluginFileValidateSize
  );

  // 既存の画像パスを取得
  const checkPathElem = document.getElementById('myHealthCheckImageSrc');
  const checkPath = checkPathElem ? checkPathElem.value : '';
  const existingImagePath = checkPath
    ? 'http://localhost:9900' + checkPath
    : null;
  originalImagePath = existingImagePath;
  console.log('📌 既存の画像パス:', existingImagePath);

  // FilePond 初期化
  const pond = FilePond.create(document.querySelector('.filepond'), {
    labelIdle: `ドラッグ＆ドロップ または <span class="filepond--label-action">ファイル選択</span>`,
    allowMultiple: false,
    maxFileSize: '50MB',
    acceptedFileTypes: ['image/*'],
    imagePreviewHeight: 200,
    imageCropAspectRatio: '1:1',
    imageResizeTargetWidth: 200,
    imageResizeTargetHeight: 200,
    stylePanelLayout: 'compact circle',
    styleLoadIndicatorPosition: 'center bottom',
    styleButtonRemoveItemPosition: 'center bottom',
    instantUpload: false,
    allowReplace: true,
  });

  // 既存の画像があれば FilePond に追加
  if (existingImagePath) {
    pond.addFiles(existingImagePath, {
      source: checkPath,
      options: { type: 'remote' },
    });
  }

  // 初期フォームデータを保存
  storeOriginalFormValues();

  function storeOriginalFormValues() {
    originalFormData = {
      height: $("input[name='height']").val(),
      weight: $("input[name='weight']").val(),
      leftEye: $("input[name='leftEye']").val(),
      rightEye: $("input[name='rightEye']").val(),
      note: $("textarea[name='note']").val(),
    };
    console.log('📌 元のフォーム値を保存:', originalFormData);
  }

  function hasFormDataChanged() {
    if (
      originalFormData.height !== $("input[name='height']").val() ||
      originalFormData.weight !== $("input[name='weight']").val() ||
      originalFormData.leftEye !== $("input[name='leftEye']").val() ||
      originalFormData.rightEye !== $("input[name='rightEye']").val() ||
      originalFormData.note !== $("textarea[name='note']").val()
    ) {
      return true;
    }

    const files = pond.getFiles();
    if (originalImagePath && files.length === 0) {
      return true;
    }
    if (files.length > 0 && files[0].source instanceof File) {
      return true;
    }
    return false;
  }

  // フォーム送信時
  $('#childRecordForm').on('submit', function (event) {
    event.preventDefault();
    console.log('📌 フォーム送信イベント');

    if (!hasFormDataChanged()) {
      Swal.fire({
        title: '変更された内容がありません。',
        icon: 'info',
        confirmButtonText: '確認',
      });
      return;
    }

    const childId = $(this).attr('data-child-id');
    const recordId = $(this).attr('data-record-id');
    console.log('📌 子どもID:', childId, '記録ID:', recordId);

    const formData = createFormData();
    submitFormData(childId, recordId, formData);
  });

  function createFormData() {
    const formData = new FormData();
    formData.append('height', $("input[name='height']").val());
    formData.append('weight', $("input[name='weight']").val());
    formData.append('leftEye', $("input[name='leftEye']").val());
    formData.append('rightEye', $("input[name='rightEye']").val());
    formData.append('note', $("textarea[name='note']").val());

    const files = pond.getFiles();
    if (files.length > 0 && files[0].source instanceof File) {
      formData.append('healthCheckImg', files[0].file);
      console.log('📌 新しいファイルが追加されました:', files[0].file.name);
    } else {
      console.log('📌 既存の画像を保持（ファイル送信なし）');
    }

    return formData;
  }

  function submitFormData(childId, recordId, formData) {
    $.ajax({
      url: `/children/${childId}/records/${recordId}/edit`,
      method: 'POST',
      data: formData,
      contentType: false,
      processData: false,
      success: function (response) {
        console.log('✅ サーバー応答:', response);
        if (response.success) {
          console.log('✅ 更新成功！ リダイレクト先:', response.redirectUrl);
          window.location.href = response.redirectUrl;
        } else {
          console.error('❌ 更新失敗');
          Swal.fire('修正に失敗しました。もう一度お試しください。');
        }
      },
      error: function (xhr) {
        console.error('❌ AJAX エラー:', xhr.responseText);
        Swal.fire('修正中にエラーが発生しました。もう一度お試しください。');
      },
    });
  }
});
