let modules = [];
let moduleIndex = 0;
let currentEditingModuleButton = null;
let lecturesPerModule = JSON.parse(localStorage.getItem("lecturesPerModule") || "{}");
let currentModuleIndex = null;
let currentEditingLesson = {
  moduleIndex: null,
  lessonIndex: null,
  domElement: null,
};

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

// Mở modal sửa module
function editModule(button) {
  currentEditingModuleButton = button;
  const currentTitle = button.closest(".accordion-header").querySelector(".accordion-button").textContent.trim();
  document.getElementById("update-module-title").value = currentTitle;
}

// Lưu thay đổi module
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
  const moduleElement = button.closest(".accordion-item");
  const header = moduleElement.querySelector(".accordion-header");
  const title = header.querySelector(".accordion-button").textContent.trim();

  // Lấy moduleIndex từ attribute data-module-index của .lesson-container
  const lessonContainer = moduleElement.querySelector(".lesson-container");
  const moduleIndex = parseInt(lessonContainer.dataset.moduleIndex);

  // Xóa khỏi mảng modules
  modules = modules.filter((m) => m !== title);
  localStorage.setItem("modules", JSON.stringify(modules));

  // ✅ Xóa luôn các bài học liên quan
  delete lecturesPerModule[moduleIndex];
  localStorage.setItem("lecturesPerModule", JSON.stringify(lecturesPerModule));

  // Xóa khỏi DOM
  moduleElement.remove();
}

// Lưu bài học vào local storage
function saveLesson() {
  const title = document.getElementById("create-lesson-title").value.trim();
  const content = document.getElementById("create-lesson-content").value.trim();
  const videoUrl = document.getElementById("create-videoUrl").value.trim();
  const duration = document.getElementById("create-videoDuration").value.trim();

  if (!title && !content && !videoUrl && !duration) {
    alert("Vui lòng nhập đầy đủ các trường!");
    return;
  }

  // Cập nhật localStorage
  if (!lecturesPerModule[currentModuleIndex]) {
    lecturesPerModule[currentModuleIndex] = [];
  }
  lecturesPerModule[currentModuleIndex].push({
    lectureTitle: title,
    content: content,
    videoUrl: videoUrl,
    duration: duration,
  });
  localStorage.setItem("lecturesPerModule", JSON.stringify(lecturesPerModule));

  const container = document.querySelector(`.lesson-container[data-module-index="${currentModuleIndex}"]`);
  const lessonIndex = container.querySelectorAll(".lesson-item").length;

  const html = `
    <div class="d-flex justify-content-between rbt-course-wrape mb-4 lesson-item">
      <!-- Input ẩn để gửi dữ liệu -->
      <input type="hidden" name="modules[${currentModuleIndex}].lectures[${lessonIndex}].lectureTitle" value="${title}" />
      <input type="hidden" name="modules[${currentModuleIndex}].lectures[${lessonIndex}].content" value="${content}" />
      <input type="hidden" name="modules[${currentModuleIndex}].lectures[${lessonIndex}].videoUrl" value="${videoUrl}" />
      <input type="hidden" name="modules[${currentModuleIndex}].lectures[${lessonIndex}].duration" value="${duration}" />

      <div class="col-10 inner d-flex align-items-center gap-2">
        <i class="feather-menu cursor-scroll"></i>
        <h6 class="rbt-title mb-0">${title}</h6>
      </div>
      <div class="col-2 inner">
        <ul class="rbt-list-style-1 rbt-course-list d-flex gap-2">
          <li><i class="feather-trash" onclick="deleteLesson(${currentModuleIndex}, ${lessonIndex}, this)"></i></li>
          <li><i class="feather-edit" data-bs-toggle="modal" data-bs-target="#update-lesson" onclick="editLesson(${currentModuleIndex}, ${lessonIndex}, this)"></i></li>
        </ul>
      </div>
    </div>
  `;

  container.insertAdjacentHTML("beforeend", html);
  bootstrap.Modal.getInstance(document.getElementById("create-lesson")).hide();
}

// Mở model sửa lesson
function editLesson(moduleIndex, lessonIndex, el) {
  currentEditingLesson = {
    moduleIndex: moduleIndex,
    lessonIndex: lessonIndex,
    domElement: el.closest(".lesson-item"),
  };

  const lesson = lecturesPerModule[moduleIndex][lessonIndex];

  document.getElementById("update-lesson-title").value = lesson.lectureTitle || "";
  document.getElementById("update-lesson-content").value = lesson.content || "";
  document.getElementById("update-videoUrl").value = lesson.videoUrl || "";
  document.getElementById("update-videoDuration").value = lesson.duration || "";
}

// Lưu thay đổi lesson
function updateLesson() {
  const title = document.getElementById("update-lesson-title").value.trim();
  const content = document.getElementById("update-lesson-content").value.trim();
  const videoUrl = document.getElementById("update-videoUrl").value.trim();
  const duration = document.getElementById("update-videoDuration").value.trim();

  const { moduleIndex, lessonIndex, domElement } = currentEditingLesson;

  if (
    moduleIndex === null ||
    lessonIndex === null ||
    !domElement ||
    !lecturesPerModule[moduleIndex] ||
    !lecturesPerModule[moduleIndex][lessonIndex]
  ) {
    alert("Không thể cập nhật bài học.");
    return;
  }

  // Cập nhật trong localStorage
  lecturesPerModule[moduleIndex][lessonIndex] = {
    lectureTitle: title,
    content: content,
    videoUrl: videoUrl,
    duration: duration,
  };
  localStorage.setItem("lecturesPerModule", JSON.stringify(lecturesPerModule));

  // Cập nhật DOM
  domElement.querySelector("h6").textContent = title;
  domElement.querySelector(`input[name="modules[${moduleIndex}].lectures[${lessonIndex}].lectureTitle"]`).value = title;
  domElement.querySelector(`input[name="modules[${moduleIndex}].lectures[${lessonIndex}].content"]`).value = content;
  domElement.querySelector(`input[name="modules[${moduleIndex}].lectures[${lessonIndex}].videoUrl"]`).value = videoUrl;
  domElement.querySelector(`input[name="modules[${moduleIndex}].lectures[${lessonIndex}].duration"]`).value = duration;

  bootstrap.Modal.getInstance(document.getElementById("update-lesson")).hide();

  currentEditingLesson = {
    moduleIndex: null,
    lessonIndex: null,
    domElement: null,
  };
}

// Xóa lesson khỏi localStorage
function deleteLesson(moduleIndex, lessonIndex, el) {
  el.closest(".lesson-item").remove();
  if (lecturesPerModule[moduleIndex]) {
    lecturesPerModule[moduleIndex].splice(lessonIndex, 1);
    localStorage.setItem("lecturesPerModule", JSON.stringify(lecturesPerModule));
  }
}

function prepareAddLesson(moduleIndex) {
  currentModuleIndex = moduleIndex;
  document.getElementById("create-lesson-title").value = "";
  document.getElementById("create-lesson-content").value = "";
  document.getElementById("create-videoUrl").value = "";
  document.getElementById("create-videoDuration").value = "";
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

                <!-- Bài học -->
                <div class="lesson-container" data-module-index="${moduleIndex}"></div>

                <div class="d-flex flex-wrap justify-content-between align-items-center">
                  <div class="gap-3 d-flex flex-wrap">
                    <button
                      class="rbt-btn btn-border hover-icon-reverse rbt-sm-btn-2"
                      type="button"
                      data-bs-toggle="modal"
                      data-bs-target="#create-lesson"
                      onclick="prepareAddLesson(${moduleIndex})"
                    >
                      <span class="icon-reverse-wrapper">
                        <span class="btn-text">Bài học</span>
                        <span class="btn-icon"><i class="feather-plus-square"></i></span>
                        <span class="btn-icon"><i class="feather-plus-square"></i></span>
                      </span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          `;
  container.insertAdjacentHTML("beforeend", html);
  moduleIndex++;

  const moduleId = moduleIndex - 1;
  const lessonContainer = document.querySelector(`.lesson-container[data-module-index="${moduleId}"]`);
  const lessons = lecturesPerModule[moduleId] || [];

  lessons.forEach((lesson, index) => {
    const lessonHtml = `
      <div class="d-flex justify-content-between rbt-course-wrape mb-4 lesson-item">
        <input type="hidden" name="modules[${moduleId}].lectures[${index}].lectureTitle" value="${lesson.lectureTitle}" />
        <input type="hidden" name="modules[${moduleId}].lectures[${index}].content" value="${lesson.content}" />
        <input type="hidden" name="modules[${moduleId}].lectures[${index}].videoUrl" value="${lesson.videoUrl}" />
        <input type="hidden" name="modules[${moduleId}].lectures[${index}].duration" value="${lesson.duration}" />

        <div class="col-10 inner d-flex align-items-center gap-2">
          <i class="feather-menu cursor-scroll"></i>
          <h6 class="rbt-title mb-0">${lesson.lectureTitle}</h6>
        </div>
        <div class="col-2 inner">
          <ul class="rbt-list-style-1 rbt-course-list d-flex gap-2">
            <li><i class="feather-trash" onclick="deleteLesson(${moduleId}, ${index}, this)"></i></li>
            <li><i class="feather-edit" data-bs-toggle="modal" data-bs-target="#update-lesson" onclick="editLesson(${moduleId}, ${index}, this)"></i></li>
          </ul>
        </div>
      </div>
    `;
    lessonContainer.insertAdjacentHTML("beforeend", lessonHtml);
  });
}

// Tải chương khóa học từ local storage khi load trang
window.addEventListener("DOMContentLoaded", () => {
  modules = JSON.parse(localStorage.getItem("modules") || "[]");
  modules.forEach((title) => loadModuleFromStorage(title));
});
