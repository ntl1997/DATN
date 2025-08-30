// ========== STORAGE ==========
const Storage = {
  get: (key, fallback = {}) => JSON.parse(localStorage.getItem(key) || JSON.stringify(fallback)),
  set: (key, value) => localStorage.setItem(key, JSON.stringify(value)),
  remove: (key) => localStorage.removeItem(key),
};

// ========== STATE ==========
let modules = Storage.get("modules", []);
let lecturesPerModule = Storage.get("lecturesPerModule", {});
let quizzesPerModule = Storage.get("quizzesPerModule", {});
let currentModuleIndex = null;
let currentEditingModuleButton = null;

// ========== HANDLER ==========
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

function setupAttachmentsHandlers() {
  const fileInput = document.getElementById("fileInput");
  const fileCountText = document.getElementById("fileCountText");

  fileInput.addEventListener("change", function () {
    const count = fileInput.files.length;
    fileCountText.textContent = count > 0 ? `Đã đính kèm ${count} tệp` : "";
  });
}

function autoResizeTextarea(textarea) {
  textarea.style.height = "auto";
  textarea.style.height = textarea.scrollHeight + "px";
}

// ========== INIT ==========
document.addEventListener("DOMContentLoaded", () => {
  setupPriceTabHandlers();
  setupAttachmentsHandlers();
  renderModules();

  // Quiz buttons
  document.getElementById("add-option").addEventListener("click", () => addOptionField());
  document.getElementById("save-question").addEventListener("click", saveQuestion);
  document.getElementById("cancel-question").addEventListener("click", () => showTab("question-list"));
  document.getElementById("next-btn-2").addEventListener("click", () => {
    showTab("question-list");
    renderQuestionList();
  });
  document.querySelector("#question-list #prev-btn").addEventListener("click", () => showTab("quiz-info"));
  document.getElementById("save-quiz").addEventListener("click", saveQuiz);
  document.getElementById("create-quiz").addEventListener("show.bs.modal", () => showTab("quiz-info"));
});

document.querySelectorAll("textarea.auto-resize").forEach((textarea) => {
  textarea.addEventListener("input", () => autoResizeTextarea(textarea));
  autoResizeTextarea(textarea); // Gọi lần đầu khi load (nếu có nội dung)
});

// ========== FORM SUBMIT ==========
document.getElementById("create-course-form").addEventListener("submit", (e) => {
  e.preventDefault();
  const form = e.target;

  // ✅ Kiểm tra xem courseData có tồn tại không (chỉ dùng ở trang edit)
  const courseId = typeof courseData !== "undefined" ? courseData.courseId : null;

  const course = {
    courseId: courseId,
    title: document.getElementById("course-title").value,
    description: document.getElementById("course-description").value,
    demoVideoUrl: document.getElementById("videoUrl").value,
    overview: document.getElementById("overview").value,
    skillLevel: document.getElementById("skillLevel").value,
    language: document.getElementById("language").value,
    categoryIds: Array.from(document.getElementById("category").selectedOptions).map((o) => +o.value),
    price: +document.getElementById("hiddenPrice").value || 0,
    discount: +document.getElementById("discountedPrice").value || 0,
    hasCertificate: document.getElementById("hasCertificate").value || false,
    modules: modules.map((mod, idx) => ({
      moduleId: mod.moduleId || null, // ✅ giữ lại moduleId nếu có
      moduleTitle: mod.moduleTitle,
      lectures: (lecturesPerModule[idx] || []).map(({ lectureTitle, content, videoUrl, duration }) => ({
        lectureTitle,
        content,
        videoUrl,
        duration: +duration || 0,
      })),
      quizzes: (quizzesPerModule[idx] || []).map(({ title, timeLimit, totalScore, quizType, questions }) => ({
        title,
        timeLimit: +timeLimit || 0,
        totalScore: +totalScore || 0,
        quizType: quizType || "regular",
        questions: questions.map(({ questionText, correctOption, score, options }) => ({
          questionText,
          correctOption,
          score: +score || 0,
          options: options.map((o) => ({
            optionText: o.optionText,
            isCorrect: !!o.isCorrect,
          })),
        })),
      })),
    })),
  };

  // console.log("====== COURSE DATA ======");
  // console.log(JSON.stringify(course, null, 2));

  document.getElementById("courseJson").value = JSON.stringify(course);
  form.submit(); // submit thật sau khi gán
});
