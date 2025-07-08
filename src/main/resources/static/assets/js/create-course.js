// Xử lý giá khóa học
document.addEventListener("DOMContentLoaded", function () {
  const regularPriceInput = document.getElementById("regularPrice");
  const hiddenPriceInput = document.getElementById("hiddenPrice");

  const paidTab = document.getElementById("paid-tab");
  const freeTab = document.getElementById("free-tab");

  // Khi người dùng nhập giá ở tab "Trả phí", cập nhật hidden input
  regularPriceInput.addEventListener("input", function () {
    hiddenPriceInput.value = this.value || 0;
  });

  // Khi nhấn vào tab "Trả phí"
  paidTab.addEventListener("click", function () {
    // Gán giá trị hiện tại của input regular vào hidden input
    hiddenPriceInput.value = regularPriceInput.value || 0;
  });

  // Khi nhấn vào tab "Miễn phí"
  freeTab.addEventListener("click", function () {
    // Gán giá trị của hidden input = 0
    hiddenPriceInput.value = 0;
  });
});

// Xử lý chứng chỉ khóa học
document.addEventListener("DOMContentLoaded", function () {
  const radioButtons = document.querySelectorAll('input[name="radio-group"]');
  const hasCertInput = document.getElementById("hasCertificate");

  radioButtons.forEach(function (radio) {
    radio.addEventListener("change", function () {
      if (this.id === "option1") {
        hasCertInput.value = 0;
      } else {
        hasCertInput.value = 1;
      }
    });
  });

  // Khởi tạo nếu có sẵn một radio được chọn
  const checkedRadio = document.querySelector('input[name="radio-group"]:checked');
  if (checkedRadio) {
    hasCertInput.value = checkedRadio.id !== "option1";
  }
});

// Xử lý thêm module khóa học
let modules = [];
let moduleIndex = 0;
let currentEditingModuleButton = null;

// Thêm chương khóa học
function addModule() {
  const moduleTitle = document.getElementById("create-module-title").value.trim();
  if (!moduleTitle) {
    alert("Vui lòng nhập tên chủ đề");
    return;
  }

  // Thêm vào mảng và localStorage
  modules.push(moduleTitle);
  localStorage.setItem("modules", JSON.stringify(modules));

  // Reset giá trị của input title module
  document.getElementById("create-module-title").value = "";

  // Tải lại giao diện
  loadModuleFromStorage(moduleTitle);
}

// Mở modal sửa module, điền tên hiện tại
function editModule(button) {
  currentEditingModuleButton = button;
  const currentTitle = button.closest(".accordion-header").querySelector(".accordion-button").textContent.trim();
  document.getElementById("update-module-title").value = currentTitle;
}

// Lưu thay đổi tên module
function updateModule() {
  const newTitle = document.getElementById("update-module-title").value.trim();
  if (!newTitle || !currentEditingModuleButton) return;

  const button = currentEditingModuleButton.closest(".accordion-header").querySelector(".accordion-button");
  const oldTitle = button.textContent.trim();

  // Cập nhật DOM
  button.textContent = newTitle;

  // Cập nhật trong mảng
  const index = modules.findIndex((m) => m === oldTitle);
  if (index !== -1) {
    modules[index] = newTitle;
    localStorage.setItem("modules", JSON.stringify(modules));
  }

  currentEditingModuleButton = null;
}

// Xóa chương khóa học
function deleteModule(button) {
  const header = button.closest(".accordion-header");
  const title = header.querySelector(".accordion-button").textContent.trim();

  // Xóa khỏi mảng
  modules = modules.filter((m) => m !== title);
  localStorage.setItem("modules", JSON.stringify(modules));

  // Xóa khỏi DOM
  button.closest(".accordion-item").remove();
}

// Tải chương khóa học từ local storage
function loadModuleFromStorage(moduleTitle) {
  const container = document.getElementById("module-container");
  const html = `
          <div class="accordion-item card mb--20">
            <h2 class="accordion-header card-header rbt-course" id="moduleHeader${moduleIndex}">
              <button class="accordion-button collapsed" type="button"
                data-bs-toggle="collapse" data-bs-target="#moduleCollapse${moduleIndex}"
                aria-expanded="false" aria-controls="moduleCollapse${moduleIndex}">
                ${moduleTitle}
              </button>
              <span class="rbt-course-icon rbt-course-edit" data-bs-toggle="modal" data-bs-target="#UpdateModuleModel" onclick="editModule(this)"></span>
              <span class="rbt-course-icon rbt-course-del" onclick="deleteModule(this)"></span>
            </h2>
            <div id="moduleCollapse${moduleIndex}" class="accordion-collapse collapse" aria-labelledby="moduleHeader${moduleIndex}">
              <div class="accordion-body card-body">
                <!-- Input ẩn để gửi moduleTitle về controller -->
                <input type="hidden" name="modules[${moduleIndex}].moduleTitle" value="${moduleTitle}" />

                <p class="text-muted">Chưa có bài học nào.</p>
                <div class="d-flex flex-wrap justify-content-between align-items-center">
                  <div class="gap-3 d-flex flex-wrap">
                    <button
                      class="rbt-btn btn-border hover-icon-reverse rbt-sm-btn-2"
                      type="button"
                      data-bs-toggle="modal"
                      data-bs-target="#Lesson"
                    >
                      <span class="icon-reverse-wrapper"
                        ><span class="btn-text">Bài học</span
                        ><span class="btn-icon"><i class="feather-plus-square"></i></span
                        ><span class="btn-icon"><i class="feather-plus-square"></i></span
                      ></span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          `;
  container.insertAdjacentHTML("beforeend", html);
  moduleIndex++;
}

// Tải chương khóa học từ local storage khi load trang
window.addEventListener("DOMContentLoaded", () => {
  modules = JSON.parse(localStorage.getItem("modules") || "[]");
  modules.forEach((title) => loadModuleFromStorage(title));
});
