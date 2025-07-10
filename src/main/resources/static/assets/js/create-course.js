// ======================= STORAGE HELPER =========================
const Storage = {
  get(key, fallback = {}) {
    return JSON.parse(localStorage.getItem(key) || JSON.stringify(fallback));
  },
  set(key, value) {
    localStorage.setItem(key, JSON.stringify(value));
  },
  remove(key) {
    localStorage.removeItem(key);
  },
};

// ======================= STATE =========================
let modules = Storage.get("modules", []);
let lecturesPerModule = Storage.get("lecturesPerModule", {});
let moduleIndex = modules.length;
let currentModuleIndex = null;
let currentEditingModuleButton = null;
let currentEditingLesson = { moduleIndex: null, lessonIndex: null, domElement: null };

// ======================= INIT =========================
document.addEventListener("DOMContentLoaded", function () {
  setupPriceTabHandlers();
  setupCertificateHandlers();
  renderModules();
});

// ======================= HANDLERS =========================
function setupPriceTabHandlers() {
  const regularPriceInput = document.getElementById("regularPrice");
  const hiddenPriceInput = document.getElementById("hiddenPrice");

  document.getElementById("paid-tab").addEventListener("click", () => {
    hiddenPriceInput.value = regularPriceInput.value || 0;
  });
  document.getElementById("free-tab").addEventListener("click", () => {
    hiddenPriceInput.value = 0;
  });
  regularPriceInput.addEventListener("input", () => {
    hiddenPriceInput.value = regularPriceInput.value || 0;
  });
}

function setupCertificateHandlers() {
  const radioButtons = document.querySelectorAll('input[name="radio-group"]');
  const hasCertInput = document.getElementById("hasCertificate");

  radioButtons.forEach((radio) => {
    radio.addEventListener("change", () => {
      hasCertInput.value = radio.id === "option1" ? 0 : 1;
    });
  });

  const checked = document.querySelector('input[name="radio-group"]:checked');
  hasCertInput.value = checked && checked.id !== "option1" ? 1 : 0;
}

// ======================= MODULE =========================
function addModule() {
  const title = document.getElementById("create-module-title").value.trim();
  if (!title) return alert("Vui lòng nhập tên chủ đề");

  modules.push(title);
  Storage.set("modules", modules);

  document.getElementById("create-module-title").value = "";
  bootstrap.Modal.getInstance(document.getElementById("CreateModuleModel")).hide();
  renderModule(title);
}

function editModule(button) {
  currentEditingModuleButton = button;
  const title = button.closest(".accordion-header").querySelector(".accordion-button").textContent.trim();
  document.getElementById("update-module-title").value = title;
}

function updateModule() {
  const newTitle = document.getElementById("update-module-title").value.trim();
  if (!newTitle || !currentEditingModuleButton) return alert("Vui lòng nhập tên chủ đề");

  const button = currentEditingModuleButton.closest(".accordion-header").querySelector(".accordion-button");
  const oldTitle = button.textContent.trim();
  button.textContent = newTitle;

  const index = modules.findIndex((m) => m === oldTitle);
  if (index !== -1) {
    modules[index] = newTitle;
    Storage.set("modules", modules);
  }
  bootstrap.Modal.getInstance(document.getElementById("UpdateModuleModel")).hide();
  currentEditingModuleButton = null;
}

function deleteModule(button) {
  const item = button.closest(".accordion-item");
  const title = item.querySelector(".accordion-button").textContent.trim();
  const lessonContainer = item.querySelector(".lesson-container");
  const index = parseInt(lessonContainer.dataset.moduleIndex);

  modules = modules.filter((m) => m !== title);
  delete lecturesPerModule[index];
  Storage.set("modules", modules);
  Storage.set("lecturesPerModule", lecturesPerModule);

  item.remove();
}

// ======================= LESSON =========================
function saveLesson() {
  const title = document.getElementById("create-lesson-title").value.trim();
  const content = document.getElementById("create-lesson-content").value.trim();
  const videoUrl = document.getElementById("create-videoUrl").value.trim();
  const duration = document.getElementById("create-videoDuration").value.trim();
  if (!title || !content || !videoUrl || !duration) return alert("Vui lòng nhập đầy đủ các trường!");

  if (!lecturesPerModule[currentModuleIndex]) lecturesPerModule[currentModuleIndex] = [];
  const lessonIndex = lecturesPerModule[currentModuleIndex].length;
  lecturesPerModule[currentModuleIndex].push({ lectureTitle: title, content, videoUrl, duration });
  Storage.set("lecturesPerModule", lecturesPerModule);

  appendLessonToDOM(currentModuleIndex, lessonIndex);
  bootstrap.Modal.getInstance(document.getElementById("create-lesson")).hide();
}

function prepareAddLesson(index) {
  currentModuleIndex = index;
  ["create-lesson-title", "create-lesson-content", "create-videoUrl", "create-videoDuration"].forEach(
    (id) => (document.getElementById(id).value = "")
  );
}

function deleteLesson(moduleIdx, lessonIdx, el) {
  el.closest(".lesson-item").remove();
  lecturesPerModule[moduleIdx].splice(lessonIdx, 1);
  Storage.set("lecturesPerModule", lecturesPerModule);
}

function editLesson(moduleIdx, lessonIdx, el) {
  currentEditingLesson = { moduleIndex: moduleIdx, lessonIndex: lessonIdx, domElement: el.closest(".lesson-item") };
  const lesson = lecturesPerModule[moduleIdx][lessonIdx];
  document.getElementById("update-lesson-title").value = lesson.lectureTitle;
  document.getElementById("update-lesson-content").value = lesson.content;
  document.getElementById("update-videoUrl").value = lesson.videoUrl;
  document.getElementById("update-videoDuration").value = lesson.duration;
}

function updateLesson() {
  const { moduleIndex, lessonIndex, domElement } = currentEditingLesson;
  if (!domElement) return alert("Không thể cập nhật bài học");

  const title = document.getElementById("update-lesson-title").value.trim();
  const content = document.getElementById("update-lesson-content").value.trim();
  const videoUrl = document.getElementById("update-videoUrl").value.trim();
  const duration = document.getElementById("update-videoDuration").value.trim();

  lecturesPerModule[moduleIndex][lessonIndex] = { lectureTitle: title, content, videoUrl, duration };
  Storage.set("lecturesPerModule", lecturesPerModule);

  domElement.querySelector("h6").textContent = title;
  ["lectureTitle", "content", "videoUrl", "duration"].forEach((key) => {
    domElement.querySelector(`input[name="modules[${moduleIndex}].lectures[${lessonIndex}].${key}"]`).value =
      lecturesPerModule[moduleIndex][lessonIndex][key];
  });
  bootstrap.Modal.getInstance(document.getElementById("update-lesson")).hide();
  currentEditingLesson = { moduleIndex: null, lessonIndex: null, domElement: null };
}

// ======================= RENDER =========================
function renderModules() {
  modules.forEach((title) => renderModule(title));
}

function renderModule(title) {
  const container = document.getElementById("module-container");
  const id = moduleIndex;
  const html = `
    <div class="accordion-item card mb--20">
      <h2 class="accordion-header card-header rbt-course">
        <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#moduleCollapse${id}">${title}</button>
        <span class="rbt-course-icon rbt-course-edit" data-bs-toggle="modal" data-bs-target="#UpdateModuleModel" onclick="editModule(this)"></span>
        <span class="rbt-course-icon rbt-course-del" onclick="deleteModule(this)"></span>
      </h2>
      <div id="moduleCollapse${id}" class="accordion-collapse collapse">
        <div class="accordion-body card-body">
          <input type="hidden" name="modules[${id}].moduleTitle" value="${title}" />
          <div class="lesson-container" data-module-index="${id}"></div>
          <div class="d-flex flex-wrap justify-content-between">
            <button class="rbt-btn btn-border rbt-sm-btn-2" type="button" data-bs-toggle="modal" data-bs-target="#create-lesson" onclick="prepareAddLesson(${id})">
              <span class="btn-text">Bài học</span>
              <span class="btn-icon"><i class="feather-plus-square"></i></span>
            </button>
          </div>
        </div>
      </div>
    </div>
  `;
  container.insertAdjacentHTML("beforeend", html);
  (lecturesPerModule[id] || []).forEach((_, idx) => appendLessonToDOM(id, idx));
  moduleIndex++;
}

function appendLessonToDOM(moduleIdx, lessonIdx) {
  const lesson = lecturesPerModule[moduleIdx][lessonIdx];
  const container = document.querySelector(`.lesson-container[data-module-index="${moduleIdx}"]`);
  const html = `
    <div class="lesson-item d-flex justify-content-between rbt-course-wrape mb-4">
      ${Object.entries(lesson)
        .map(
          ([k, v]) => `<input type="hidden" name="modules[${moduleIdx}].lectures[${lessonIdx}].${k}" value="${v}" />`
        )
        .join("\n")}
      <div class="col-10 inner d-flex align-items-center gap-2">
        <i class="feather-menu cursor-scroll"></i>
        <h6 class="rbt-title mb-0">${lesson.lectureTitle}</h6>
      </div>
      <div class="col-2 inner">
        <ul class="rbt-list-style-1 rbt-course-list d-flex gap-2">
          <li><i class="feather-trash" onclick="deleteLesson(${moduleIdx}, ${lessonIdx}, this)"></i></li>
          <li><i class="feather-edit" data-bs-toggle="modal" data-bs-target="#update-lesson" onclick="editLesson(${moduleIdx}, ${lessonIdx}, this)"></i></li>
        </ul>
      </div>
    </div>
  `;
  container.insertAdjacentHTML("beforeend", html);
}
