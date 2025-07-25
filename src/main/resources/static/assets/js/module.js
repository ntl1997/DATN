function addModule() {
  const input = document.getElementById("create-module-title");
  const title = input.value.trim();
  if (!title) return alert("Vui lòng nhập tên chủ đề");

  modules.push({ moduleId: null, moduleTitle: title });
  Storage.set("modules", modules);
  input.value = "";
  bootstrap.Modal.getInstance(document.getElementById("CreateModuleModel")).hide();
  renderModule(modules.length - 1);
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

  const index = modules.findIndex((m) => m.moduleTitle === oldTitle);
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
  const index = parseInt(item.querySelector(".lesson-container").dataset.moduleIndex);

  modules = modules.filter((m) => m.moduleTitle !== title);
  delete lecturesPerModule[index];
  delete quizzesPerModule[index];
  Storage.set("modules", modules);
  Storage.set("lecturesPerModule", lecturesPerModule);
  Storage.set("quizzesPerModule", quizzesPerModule);

  item.remove();
}

function renderModules() {
  modules.forEach((_, index) => renderModule(index));
}

function renderModule(index) {
  const container = document.getElementById("module-container");
  const title = modules[index].moduleTitle;
  const html = `
    <div class="accordion-item card mb--20">
      <h2 class="accordion-header card-header rbt-course">
        <button class="accordion-button collapsed" type="button"
          data-bs-toggle="collapse" data-bs-target="#moduleCollapse${index}">
          ${title}
        </button>
        <span class="rbt-course-icon rbt-course-edit" data-bs-toggle="modal"
          data-bs-target="#UpdateModuleModel" onclick="editModule(this)">
        </span>
        <span class="rbt-course-icon rbt-course-del" onclick="deleteModule(this)"></span>
      </h2>
      <div id="moduleCollapse${index}" class="accordion-collapse collapse">
        <div class="accordion-body card-body">
          <div class="lesson-container" data-module-index="${index}"></div>
          <div class="d-flex flex-wrap justify-content-between align-items-center">
            <div class="gap-3 d-flex flex-wrap">
              <button class="rbt-btn btn-border rbt-sm-btn-2" type="button"
                data-bs-toggle="modal" data-bs-target="#create-lesson"
                onclick="prepareAddLesson(${index})">
                <span class="btn-text">Bài học</span>
                <span class="btn-icon"><i class="feather-plus-square"></i></span>
              </button>
              <button class="rbt-btn btn-border rbt-sm-btn-2" type="button"
                onclick="createEmptyQuiz(${index})">
                <span class="btn-text">Quiz</span>
                <span class="btn-icon"><i class="feather-plus-square"></i></span>
              </button>
              <button class="rbt-btn btn-border rbt-sm-btn-2" type="button"
                onclick="createEmptyQuiz(${index}, 'assignment')">
                <span class="btn-text">Assignment</span>
                <span class="btn-icon"><i class="feather-plus-square"></i></span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `;
  container.insertAdjacentHTML("beforeend", html);
  (lecturesPerModule[index] || []).forEach((_, idx) => appendLessonToDOM(index, idx));
  (quizzesPerModule[index] || []).forEach((_, idx) => appendQuizToDOM(index, idx));
}
