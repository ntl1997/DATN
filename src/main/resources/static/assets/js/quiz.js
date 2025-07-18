// ======================= quiz.js =======================

let quizDraft = { title: "", timeLimit: 0, totalScore: 100, questions: [] };
let currentQuestionIndex = null;
let correctOptionIndex = null;
let editingQuizInfo = null;

function resetQuestionForm() {
  correctOptionIndex = null;
  currentQuestionIndex = null;
  document.getElementById("question-text").value = "";
  document.getElementById("options-container").innerHTML = "";
  addOptionField();
}

function addOptionField(text = "", isCorrect = false) {
  const container = document.getElementById("options-container");
  const index = container.children.length;

  const div = document.createElement("div");
  div.className = "option-item d-flex align-items-center gap-2 mb--10";

  const input = document.createElement("input");
  input.type = "text";
  input.className = "form-control";
  input.placeholder = `Tùy chọn ${index + 1}`;
  input.value = text;

  const markBtn = document.createElement("button");
  markBtn.type = "button";
  markBtn.className = "btn btn-outline-success mark-correct";
  markBtn.innerHTML = "✔";
  markBtn.title = "Chọn làm đáp án đúng";

  const removeBtn = document.createElement("button");
  removeBtn.className = "btn btn-outline-danger remove-option";
  removeBtn.type = "button";
  removeBtn.innerHTML = `<i class='feather-x'></i>`;

  div.append(input, markBtn, removeBtn);
  container.appendChild(div);

  if (isCorrect) markCorrectOption(div);

  markBtn.addEventListener("click", () => markCorrectOption(div));
  removeBtn.addEventListener("click", () => {
    const idx = Array.from(container.children).indexOf(div);
    if (idx === correctOptionIndex) correctOptionIndex = null;
    div.remove();
  });
}

function markCorrectOption(selectedDiv) {
  document.querySelectorAll("#options-container .option-item").forEach((item, idx) => {
    const btn = item.querySelector(".mark-correct");
    if (item === selectedDiv) {
      btn.classList.add("btn-success");
      btn.classList.remove("btn-outline-success");
      correctOptionIndex = idx;
    } else {
      btn.classList.remove("btn-success");
      btn.classList.add("btn-outline-success");
    }
  });
}

function updateAllQuestionScores() {
  const total = quizDraft.totalScore || 100;
  const count = quizDraft.questions.length;
  const score = count ? +(total / count).toFixed(2) : 0;
  quizDraft.questions.forEach((q) => (q.score = score));
}

function saveQuestion() {
  const questionText = document.getElementById("question-text").value.trim();
  const optionsEls = document.querySelectorAll("#options-container .option-item");

  const options = [];
  let correctIndex = -1;

  optionsEls.forEach((el, idx) => {
    const text = el.querySelector("input[type=text]").value.trim();
    const isCorrect = el.querySelector(".mark-correct").classList.contains("btn-success");
    if (text) options.push({ optionText: text, isCorrect });
    if (isCorrect) correctIndex = idx;
  });

  if (!questionText || options.length < 2 || correctIndex === -1) {
    return alert("Hãy nhập câu hỏi, ít nhất 2 đáp án và chọn đáp án đúng.");
  }

  const totalScore = quizDraft.totalScore || 100;
  const score = +(totalScore / (quizDraft.questions.length + (currentQuestionIndex === null ? 1 : 0))).toFixed(2);
  const question = {
    questionText,
    correctOption: correctIndex,
    score,
    options,
  };

  if (currentQuestionIndex !== null) {
    quizDraft.questions[currentQuestionIndex] = question;
  } else {
    quizDraft.questions.push(question);
  }

  updateAllQuestionScores();
  resetQuestionForm();
  renderQuestionList();
  showTab("question-list");
}

function renderQuestionList() {
  const container = document.getElementById("question-items");
  container.innerHTML = "";

  quizDraft.questions.forEach((q, index) => {
    const div = document.createElement("div");
    div.className = "d-flex justify-content-between rbt-course-wrape mb-4";
    div.innerHTML = `
      <div class="inner d-flex align-items-center gap-2">
        <h6 class="rbt-title mb-0">${q.questionText}</h6>
      </div>
      <div class="inner">
        <ul class="rbt-list-style-1 rbt-course-list d-flex gap-3 align-items-center">
          <li><span>Chọn 1 đáp án đúng</span></li>
          <li>
            <button class="btn quiz-modal__edit-btn dropdown-toggle me-2" data-bs-toggle="dropdown">
              <i class="feather-edit"></i>
            </button>
            <ul class="dropdown-menu">
              <li><a class="dropdown-item edit-item" onclick="editQuestion(${index})"><i class="feather-edit-2"></i> Edit</a></li>
              <li><a class="dropdown-item delete-item" onclick="deleteQuestion(${index})"><i class="feather-trash"></i> Delete</a></li>
            </ul>
          </li>
        </ul>
      </div>
    `;
    container.appendChild(div);
  });
}

function editQuestion(index) {
  const q = quizDraft.questions[index];
  currentQuestionIndex = index;
  correctOptionIndex = q.correctOption;

  document.getElementById("question-text").value = q.questionText;
  document.getElementById("options-container").innerHTML = "";
  q.options.forEach((ans) => addOptionField(ans.questionText, ans.isCorrect));
  showTab("question-options");
}

function deleteQuestion(index) {
  quizDraft.questions.splice(index, 1);
  updateAllQuestionScores();
  renderQuestionList();
}

function appendQuizToDOM(moduleIdx, quizIdx) {
  const quiz = quizzesPerModule[moduleIdx][quizIdx];
  const container = document.querySelector(`.lesson-container[data-module-index="${moduleIdx}"]`);
  const html = `
    <div class="lesson-item d-flex justify-content-between rbt-course-wrape mb-4">
      <input type="hidden" name="modules[${moduleIdx}].quizzes[${quizIdx}].title" value="${quiz.title}" />
      <input type="hidden" name="modules[${moduleIdx}].quizzes[${quizIdx}].timeLimit" value="${quiz.timeLimit}" />
      <input type="hidden" name="modules[${moduleIdx}].quizzes[${quizIdx}].totalScore" value="${quiz.totalScore}" />
      <input type="hidden" name="modules[${moduleIdx}].quizzes[${quizIdx}].questions" value='${JSON.stringify(
    quiz.questions
  )}' />
      <div class="col-10 inner d-flex align-items-center gap-2">
        <i class="feather-menu text-success"></i>
        <h6 class="rbt-title mb-0 text-success">Quiz: ${quiz.title}</h6>
      </div>
      <div class="col-2 inner">
        <ul class="rbt-list-style-1 rbt-course-list d-flex gap-2">
          <li><i class="feather-edit" data-bs-toggle="modal" data-bs-target="#create-quiz" onclick="editQuiz(${moduleIdx}, ${quizIdx})"></i></li>
          <li><i class="feather-trash" onclick="deleteQuiz(${moduleIdx}, ${quizIdx}, this)"></i></li>
        </ul>
      </div>
    </div>
  `;
  container.insertAdjacentHTML("beforeend", html);
}

function saveQuiz() {
  const title = document.getElementById("quiz-title").value.trim();
  const timeLimit = parseInt(document.getElementById("quiz-time-limit").value.trim(), 10);

  if (!title || isNaN(timeLimit) || quizDraft.questions.length === 0) {
    return alert("Vui lòng nhập tiêu đề, thời lượng và ít nhất 1 câu hỏi.");
  }

  quizDraft.title = title;
  quizDraft.timeLimit = timeLimit;
  quizDraft.totalScore = 100; // Mặc định

  if (!quizzesPerModule[currentModuleIndex]) quizzesPerModule[currentModuleIndex] = [];

  if (editingQuizInfo) {
    const { moduleIndex, quizIndex } = editingQuizInfo;
    quizzesPerModule[moduleIndex][quizIndex] = { ...quizDraft };
  } else {
    quizzesPerModule[currentModuleIndex].push({ ...quizDraft });
  }

  Storage.set("quizzesPerModule", quizzesPerModule);

  const container = document.querySelector(`.lesson-container[data-module-index="${currentModuleIndex}"]`);
  container.innerHTML = "";
  (lecturesPerModule[currentModuleIndex] || []).forEach((_, idx) => appendLessonToDOM(currentModuleIndex, idx));
  (quizzesPerModule[currentModuleIndex] || []).forEach((_, idx) => appendQuizToDOM(currentModuleIndex, idx));

  quizDraft = { title: "", timeLimit: 0, totalScore: 100, questions: [] };
  editingQuizInfo = null;
  document.getElementById("quiz-form").reset();
  document.getElementById("question-items").innerHTML = "";
  bootstrap.Modal.getInstance(document.getElementById("create-quiz")).hide();
}

function editQuiz(moduleIdx, quizIdx) {
  const quiz = quizzesPerModule[moduleIdx][quizIdx];
  editingQuizInfo = { moduleIndex: moduleIdx, quizIndex: quizIdx };
  quizDraft = JSON.parse(JSON.stringify(quiz));

  document.getElementById("quiz-title").value = quiz.title;
  document.getElementById("quiz-time-limit").value = quiz.timeLimit;

  renderQuestionList();
  showTab("question-list");
}

function deleteQuiz(moduleIdx, quizIdx, el) {
  el.closest(".lesson-item").remove();
  quizzesPerModule[moduleIdx].splice(quizIdx, 1);
  Storage.set("quizzesPerModule", quizzesPerModule);
}

function showTab(tabName) {
  ["quiz-info", "question-list", "question-options"].forEach((tab) => {
    document.getElementById(tab).classList.add("d-none");
  });
  document.getElementById(tabName).classList.remove("d-none");
}
