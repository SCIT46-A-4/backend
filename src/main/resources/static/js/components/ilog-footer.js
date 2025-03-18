// ilog-footer.js
const template = document.createElement('template');
template.innerHTML = `
  <style>
    /* footer.css 내용 */
    footer {
      background-color: var(--primary);
      color: black;
      padding: 2rem 1.5rem;
      border-top: 5px solid var(--secondary);
    }
    footer .logo-container {
      display: flex;
      align-items: center;
      margin-bottom: 1rem;
    }
    footer .footer-logo {
      height: 40px;
      margin-right: 1rem;
    }
    footer .footer-title {
      font-size: 1.2rem;
      font-weight: bold;
      color: #333;
    }
    footer .footer-links a {
      color: var(--footer-text);
      margin-right: 1.5rem;
      transition: color 0.3s;
    }
    footer .footer-links a:hover {
      color: #3273dc;
    }
    footer .social-icons {
      font-size: 1.5rem;
      margin-top: 1rem;
    }
    footer .social-icons a {
      color: var(--footer-text);
      margin-right: 1rem;
      transition: color 0.3s;
    }
    footer .social-icons a:hover {
      color: #3273dc;
    }
    footer .footer-bottom {
      margin-top: 2rem;
      padding-top: 1rem;
      border-top: 1px solid #ddd;
      font-size: 0.9rem;
    }
    footer .contact-info {
      margin-top: 1rem;
    }
    footer .contact-info p {
      margin-bottom: 0.5rem;
    }
    @media screen and (max-width: 768px) {
      footer .columns {
        display: block;
      }
      footer .column {
        width: 100%;
        margin-bottom: 1.5rem;
      }
    }
  </style>
  <footer>
    <div class="container">
      <div class="columns">
        <div class="column is-4">
          <div class="logo-container">
            <img class="footer-logo" src="../../static/images/wilog.png" alt="iLog 로고" />
            <span class="footer-title">iLog</span>
          </div>
          <p>
            아이의 감정과 발달을 체계적으로 기록하고 분석하여 건강한 성장을 돕는 서비스입니다.
          </p>
        </div>
        <div class="column is-4"></div>
        <div class="column is-4">
          <h3 class="title is-5">연락처</h3>
          <div class="contact-info">
            <p><i class="fas fa-map-marker-alt"></i> 서울특별시 강남구 테헤란로 123</p>
            <p><i class="fas fa-phone"></i> 02-123-4567</p>
            <p><i class="fas fa-envelope"></i> support@ilog.co.kr</p>
            <p><i class="fas fa-clock"></i> 평일 09:00 - 18:00 (공휴일 제외)</p>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <div class="columns is-vcentered">
          <div class="column is-4"></div>
          <div class="column is-4 has-text-centered">
            <p>
              <a href="/terms">이용약관</a> |
              <a href="/privacy"><strong>개인정보처리방침</strong></a> |
              <a href="/youth-policy">청소년보호정책</a>
            </p>
          </div>
          <div class="column is-4 has-text-right">
            <p>&copy; 2025 iLog. All rights reserved.</p>
          </div>
        </div>
      </div>
    </div>
  </footer>
`;

class IlogFooter extends HTMLElement {
  constructor() {
    super();
    this.attachShadow({ mode: 'open' });
    this.shadowRoot.appendChild(template.content.cloneNode(true));
  }
}

customElements.define('ilog-footer', IlogFooter);
