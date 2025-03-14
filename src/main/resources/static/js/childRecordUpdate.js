$(document).ready(function () {
  console.log('✅ Document loaded');

  // Initialize variables to track the original form values
  let originalFormData = {};
  let originalImagePath = '';

  // Register FilePond plugins
  FilePond.registerPlugin(
    FilePondPluginImagePreview,
    FilePondPluginFileValidateType,
    FilePondPluginFileValidateSize
  );

  // Get the existing image path
  const checkPathElem = document.getElementById('myHealthCheckImageSrc');
  const checkPath = checkPathElem ? checkPathElem.value : '';
  /*  const checkPath = document.getElementById('myHealthCheckImageSrc').value;*/
  const existingImagePath = checkPath
    ? 'http://localhost:9900/healthCheckImages' + checkPath
    : null;
  originalImagePath = existingImagePath;
  console.log('📌 Existing image path:', existingImagePath);

  // Initialize FilePond
  const pond = FilePond.create(document.querySelector('.filepond'), {
    labelIdle: `드래그 앤 드롭 또는 <span class="filepond--label-action">파일 선택</span>`,
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
    allowReplace: true, // Allow image replacement
  });

  // Add existing image to FilePond if it exists
  if (existingImagePath) {
    pond.addFiles(existingImagePath, {
      type: 'local',
      source: existingImagePath,
      options: {
        type: 'local',
      },
    });
  }

  // Store original form values after initialization
  storeOriginalFormValues();

  // Function to store original form values
  function storeOriginalFormValues() {
    originalFormData = {
      height: $("input[name='height']").val(),
      weight: $("input[name='weight']").val(),
      leftEye: $("input[name='leftEye']").val(),
      rightEye: $("input[name='rightEye']").val(),
      note: $("textarea[name='note']").val(),
    };
    console.log('📌 Original form values stored:', originalFormData);
  }

  // Function to check if form data has changed
  function hasFormDataChanged() {
    // Check text inputs and textarea
    if (
      originalFormData.height !== $("input[name='height']").val() ||
      originalFormData.weight !== $("input[name='weight']").val() ||
      originalFormData.leftEye !== $("input[name='leftEye']").val() ||
      originalFormData.rightEye !== $("input[name='rightEye']").val() ||
      originalFormData.note !== $("textarea[name='note']").val()
    ) {
      return true;
    }

    // Check if image has changed
    const files = pond.getFiles();

    // 원래 이미지가 있었는데, 지금은 파일이 없으면 삭제된 것으로 판단
    if (originalImagePath && files.length === 0) {
      return true;
    }
    //지금 새로운 파일이 업로드 된 상태인지 아닌지를 확인하는 것
    if (files.length > 0 && files[0].source instanceof File) {
      return true;
    }
    // If we reach here, nothing has changed
    return false;
  }

  // Form submission handler
  $('#childRecordForm').on('submit', function (event) {
    event.preventDefault();
    console.log('📌 Form submission event triggered');

    // Check if any data has changed
    if (!hasFormDataChanged()) {
      Swal.fire({
        title: '변경된 내용이 없습니다.',
        icon: 'info',
        confirmButtonText: '확인',
      });
      return;
    }

    // Get form identifiers
    const childId = $(this).attr('data-child-id');
    const recordId = $(this).attr('data-record-id');
    console.log('📌 Child ID:', childId, 'Record ID:', recordId);

    // Create and populate FormData
    const formData = createFormData();

    // Send AJAX request with the form data
    submitFormData(childId, recordId, formData);
  });

  // Function to create and populate FormData
  function createFormData() {
    const formData = new FormData();

    // Add form field values
    formData.append('height', $("input[name='height']").val());
    formData.append('weight', $("input[name='weight']").val());
    formData.append('leftEye', $("input[name='leftEye']").val());
    formData.append('rightEye', $("input[name='rightEye']").val());
    formData.append('note', $("textarea[name='note']").val());

    // Check if a new file was added
    const files = pond.getFiles();
    if (files.length > 0 && files[0].source instanceof File) {
      formData.append('healthCheckImg', files[0].file);
      // formData.append('fileName', files[0].file.name);
      console.log('📌 New file added:', files[0].file.name);
    } else {
      console.log('📌 Keeping existing image (no file transfer)');
    }

    return formData;
  }

  // Function to submit the form data via AJAX
  function submitFormData(childId, recordId, formData) {
    $.ajax({
      url: `/children/${childId}/records/${recordId}/edit`,
      type: 'POST',
      data: formData,
      contentType: false,
      processData: false,
      success: function (response) {
        console.log('✅ Server response:', response);
        if (response.success) {
          console.log(
            '✅ Update successful! Redirecting to:',
            response.redirectUrl
          );
          window.location.href = response.redirectUrl;
        } else {
          console.error('❌ Update failed');
          alert('수정에 실패했습니다. 다시 시도해 주세요.');
        }
      },
      error: function (xhr) {
        console.error('❌ AJAX error:', xhr.responseText);
        alert('수정 중 오류가 발생했습니다. 다시 시도해 주세요.');
      },
    });
  }
});
