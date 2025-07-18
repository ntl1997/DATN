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
  setupAttachmentsHandlers();
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

function setupAttachmentsHandlers() {
  const fileInput = document.getElementById("fileInput");
  const fileCountText = document.getElementById("fileCountText");

  fileInput.addEventListener("change", function () {
    const count = fileInput.files.length;
    if (count > 0) {
      fileCountText.textContent = `Đã đính kèm ${count} tệp`;
    } else {
      fileCountText.textContent = "";
    }
  });
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
          <div class="d-flex flex-wrap justify-content-between align-items-center">
            <div class="gap-3 d-flex flex-wrap">
              <button class="rbt-btn btn-border rbt-sm-btn-2" type="button" data-bs-toggle="modal" data-bs-target="#create-lesson" onclick="prepareAddLesson(${id})">
                <span class="btn-text">Bài học</span>
                <span class="btn-icon"><i class="feather-plus-square"></i></span>
              </button>
              <button class="rbt-btn btn-border rbt-sm-btn-2" type="button" data-bs-toggle="modal" data-bs-target="#create-quiz" onclick="prepareAddLesson(${id})">
                <span class="btn-text">Quiz</span>
                <span class="btn-icon"><i class="feather-plus-square"></i></span>
              </button>
              <button class="rbt-btn btn-border rbt-sm-btn-2" type="button" data-bs-toggle="modal" data-bs-target="#create-assignment" onclick="prepareAddLesson(${id})">
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

// ======================= QUIZ HANDLERS =========================
let quizDraft = { title: "", duration: 0, questions: [] };
let currentQuestionIndex = null;

// =============== QUIZ ===============
function saveQuizToStorage() {
  const allQuizzes = JSON.parse(localStorage.getItem("allQuizzes") || "[]");
  allQuizzes.push(quizDraft);
  localStorage.setItem("allQuizzes", JSON.stringify(allQuizzes));

  // Reset lại draft
  quizDraft = { title: "", duration: 0, questions: [] };
  localStorage.removeItem("quizQuestions");
}

// =============== UI – RESET FORM ===============
function resetQuestionForm(questionId = Date.now()) {
  correctAnswerIndex = null;
  currentQuestionIndex = null;

  document.getElementById("question-title").value = "";
  document.getElementById("answers-container").innerHTML = "";
  addAnswerField("", false, questionId); // ít nhất 1 dòng
}

// =============== UI – THÊM 1 DÒNG ĐÁP ÁN ===============
function addAnswerField(text = "", isCorrect = false) {
  const container = document.getElementById("answers-container");
  const index = container.children.length;

  const div = document.createElement("div");
  div.className = "answer-item d-flex align-items-center gap-2 mb--10";

  // Nút chọn đáp án đúng
  const markBtn = document.createElement("button");
  markBtn.type = "button";
  markBtn.className = "btn btn-outline-success mark-correct";
  markBtn.title = "Chọn làm đáp án đúng";
  markBtn.innerHTML = "✔";

  // Input text đáp án
  const input = document.createElement("input");
  input.type = "text";
  input.className = "form-control";
  input.placeholder = `Tùy chọn ${index + 1}`;
  input.value = text;

  // Nút xóa
  const removeBtn = document.createElement("button");
  removeBtn.className = "btn btn-outline-danger remove-answer";
  removeBtn.type = "button";
  removeBtn.innerHTML = `<i class="feather-x"></i>`;

  // Append tất cả vào dòng
  div.appendChild(input);
  div.appendChild(markBtn);
  div.appendChild(removeBtn);
  container.appendChild(div);

  // Nếu là đáp án đúng thì đánh dấu
  if (isCorrect) {
    markCorrectAnswer(div);
  }

  // Gắn sự kiện
  markBtn.addEventListener("click", () => {
    markCorrectAnswer(div);
  });

  removeBtn.addEventListener("click", () => {
    const index = Array.from(container.children).indexOf(div);
    if (index === correctAnswerIndex) correctAnswerIndex = null;
    div.remove();
  });
}

function markCorrectAnswer(selectedDiv) {
  const items = document.querySelectorAll("#answers-container .answer-item");

  items.forEach((item, idx) => {
    const markBtn = item.querySelector(".mark-correct");
    if (item === selectedDiv) {
      markBtn.classList.add("btn-success");
      markBtn.classList.remove("btn-outline-success");
      correctAnswerIndex = idx;
    } else {
      markBtn.classList.remove("btn-success");
      markBtn.classList.add("btn-outline-success");
    }
  });
}

// =============== LƯU CÂU HỎI ===============
function saveQuestion() {
  const title = document.getElementById("question-title").value.trim();
  const answersEls = document.querySelectorAll("#answers-container .answer-item");

  const answers = [];
  let correctIndex = -1;

  answersEls.forEach((el, idx) => {
    const text = el.querySelector("input[type=text]").value.trim();
    if (text) answers.push(text);
    if (el.querySelector(".mark-correct").classList.contains("btn-success")) {
      correctIndex = idx;
    }
  });

  if (!title || answers.length < 2 || correctIndex === -1) {
    return alert("Hãy nhập câu hỏi, ít nhất 2 đáp án và chọn đáp án đúng.");
  }

  const question = {
    title,
    correctAnswer: correctIndex,
    answers: answers,
  };

  if (currentQuestionIndex !== null) {
    quizDraft.questions[currentQuestionIndex] = question;
  } else {
    quizDraft.questions.push(question);
  }

  resetQuestionForm(Date.now());
  renderQuestionList();
  showTab("question-list");
}

// =============== SỬA CÂU HỎI ===============
function editQuestion(index) {
  const question = quizDraft.questions[index];
  currentQuestionIndex = index;
  correctAnswerIndex = question.correctAnswer;

  document.getElementById("question-title").value = question.title;
  document.getElementById("answers-container").innerHTML = "";

  question.answers.forEach((text, i) => {
    addAnswerField(text, i === question.correctAnswer, index);
  });

  showTab("question-answers");
}

// =============== HIỂN THỊ DANH SÁCH CÂU HỎI ===============
function renderQuestionList() {
  const container = document.getElementById("question-items");
  container.innerHTML = "";

  quizDraft.questions.forEach((q, index) => {
    const div = document.createElement("div");
    div.className = "d-flex justify-content-between rbt-course-wrape mb-4";
    div.innerHTML = `
      <div class="inner d-flex align-items-center gap-2">
        <h6 class="rbt-title mb-0">${q.title}</h6>
      </div>
      <div class="inner">
        <ul class="rbt-list-style-1 rbt-course-list d-flex gap-3 align-items-center">
          <li><span>Chọn 1 đáp án đúng</span></li>
          
          <li>
            <button
              type="button"
              class="btn quiz-modal__edit-btn dropdown-toggle me-2"
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              <i class="feather-edit"></i>
            </button>
            <ul class="dropdown-menu">
              <li>
                <a class="dropdown-item edit-item" onclick="editQuestion(${index})">
                  <i class="feather-edit-2"></i> Edit
                </a>
              </li>
              <li>
                <a class="dropdown-item delete-item" onclick="deleteQuestion(${index})">
                  <i class="feather-trash"></i> Delete
                </a>
              </li>
            </ul>
          </li>
        </ul>
      </div>
    `;
    container.appendChild(div);
  });
}

// =============== XÓA CÂU HỎI ===============
function deleteQuestion(index) {
  quizDraft.questions.splice(index, 1);
  renderQuestionList();
}

// =============== LƯU QUIZ ===============
function saveQuiz() {
  const title = document.getElementById("modal-field-1").value.trim();
  const duration = parseInt(document.getElementById("modal-field-2").value.trim(), 10);

  if (!title || isNaN(duration) || quizDraft.questions.length === 0) {
    return alert("Vui lòng nhập tiêu đề, thời lượng và ít nhất 1 câu hỏi.");
  }

  quizDraft.title = title;
  quizDraft.duration = duration;

  saveQuizToStorage();
  document.getElementById("quiz-form").reset();
  document.getElementById("question-items").innerHTML = "";
  alert("✅ Quiz đã được lưu!");
  bootstrap.Modal.getInstance(document.getElementById("create-quiz")).hide();
}

document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("add-answer").addEventListener("click", () => {
    addAnswerField("", false, Date.now());
  });

  document.getElementById("save-question").addEventListener("click", saveQuestion);
  document.getElementById("cancel-question").addEventListener("click", () => showTab("question-list"));

  document.getElementById("next-btn-2").addEventListener("click", () => {
    showTab("question-list");
    renderQuestionList();
  });

  document.querySelector("#question-list #prev-btn").addEventListener("click", () => {
    showTab("quiz-info");
  });

  document.querySelector("#question-list .btn-1").addEventListener("click", () => {
    resetQuestionForm(Date.now());
    showTab("question-answers");
  });

  document.getElementById("save-quiz").addEventListener("click", saveQuiz);

  document.getElementById("create-quiz").addEventListener("show.bs.modal", () => {
    showTab("quiz-info");
  });
});

function showTab(tabName) {
  const tabs = ["quiz-info", "question-list", "question-answers"];
  tabs.forEach((name) => {
    document.getElementById(name).classList.add("d-none");
  });
  document.getElementById(tabName).classList.remove("d-none");
}

// ======================= FORM DATA CHECKER =========================
document.getElementById("create-course-form").addEventListener("submit", function (e) {
  e.preventDefault(); // Ngăn form gửi về backend

  const formData = new FormData(this);
  const data = {};

  formData.forEach((value, key) => {
    data[key] = value;
  });

  console.log("Dữ liệu form:", data);
});

document.getElementById("create-course-form").addEventListener("submit", function (e) {
  e.preventDefault();

  const course = {};

  // ====== 1. DỮ LIỆU KHÓA HỌC CHUNG ======
  course.title = document.getElementById("course-title").value;
  course.description = document.getElementById("course-description").value;
  course.demoVideoUrl = document.getElementById("videoUrl").value;
  course.overview = document.getElementById("overview").value;
  course.skillLevel = document.getElementById("skillLevel").value;
  course.language = document.getElementById("language").value;
  course.categoryIds = Array.from(document.getElementById("category").selectedOptions).map((o) => +o.value);
  course.price = +document.getElementById("hiddenPrice").value || 0;
  course.discount = +document.getElementById("discountedPrice").value || 0;
  course.hasCertificate = +document.getElementById("hasCertificate").value || 0;

  // ====== 2. MODULES & LECTURES ======
  course.modules = modules.map((moduleTitle, moduleIdx) => {
    const lectures = (lecturesPerModule[moduleIdx] || []).map((lecture) => ({
      lectureTitle: lecture.lectureTitle,
      content: lecture.content,
      videoUrl: lecture.videoUrl,
      duration: +lecture.duration || 0,
    }));
    return { moduleTitle, lectures };
  });

  // ====== 3. QUIZZES ======
  const allQuizzes = JSON.parse(localStorage.getItem("allQuizzes") || "[]");
  course.quizzes = allQuizzes.map((q) => ({
    title: q.title,
    duration: q.duration,
    questions: q.questions.map((ques) => ({
      title: ques.title,
      correctAnswer: ques.correctAnswer,
      options: ques.answers.map((opt, i) => ({
        content: opt,
        isCorrect: i === ques.correctAnswer,
      })),
    })),
  }));

  // ✅ Hiển thị ra console để kiểm tra
  console.log("====== DỮ LIỆU KHÓA HỌC GỬI VỀ ======");
  console.log(course);
  console.log("======================================");
  // ✅ Nếu muốn xem đẹp hơn
  console.log(JSON.stringify(course, null, 2));

  // // GỬI VỀ BACKEND
  // fetch("/instructor/create-course", {
  //   method: "POST",
  //   headers: { "Content-Type": "application/json" },
  //   body: JSON.stringify(course),
  // })
  //   .then((res) => res.json())
  //   .then((result) => {
  //     alert("Khóa học đã được tạo thành công!");
  //     console.log(result);
  //   })
  //   .catch((err) => {
  //     console.error("❌ Lỗi khi gửi JSON:", err);
  //   });
});
