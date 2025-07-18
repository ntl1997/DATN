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
    fileCountText.textContent = count > 0 ? `Đã đính kèm ${count} tệp` : "";
  });
}

// ========== INIT ==========
document.addEventListener("DOMContentLoaded", () => {
  setupPriceTabHandlers();
  setupCertificateHandlers();
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
  document.querySelector("#question-list .btn-1").addEventListener("click", () => {
    resetQuestionForm();
    showTab("question-options");
  });
  document.getElementById("save-quiz").addEventListener("click", saveQuiz);
  document.getElementById("create-quiz").addEventListener("show.bs.modal", () => showTab("quiz-info"));
});

// ========== FORM SUBMIT ==========
document.getElementById("create-course-form").addEventListener("submit", (e) => {
  e.preventDefault();
  const courseId = courseData.courseId || null;
  const form = e.target;
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
    hasCertificate: +document.getElementById("hasCertificate").value || 0,
    modules: modules.map((title, idx) => ({
      moduleTitle: title,
      lectures: (lecturesPerModule[idx] || []).map(({ lectureTitle, content, videoUrl, duration }) => ({
        lectureTitle,
        content,
        videoUrl,
        duration: +duration || 0,
      })),
      quizzes: quizzesPerModule[idx] || [],
    })),
  };

  // console.log("====== COURSE DATA ======");
  // console.log(JSON.stringify(course, null, 2));

  document.getElementById("courseJson").value = JSON.stringify(course);
  form.submit(); // submit thật sau khi gán
});
