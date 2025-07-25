// ======================= lesson.js =======================

let currentEditingLesson = { moduleIndex: null, lessonIndex: null, domElement: null };

function prepareAddLesson(index) {
  currentModuleIndex = index;
  ["create-lesson-title", "create-lesson-content", "create-videoUrl", "create-videoDuration"].forEach(
    (id) => (document.getElementById(id).value = "")
  );
}

function saveLesson() {
  const lectureTitle = document.getElementById("create-lesson-title").value.trim();
  const content = document.getElementById("create-lesson-content").value.trim();
  const videoUrl = document.getElementById("create-videoUrl").value.trim();
  const duration = document.getElementById("create-videoDuration").value.trim();
  if (!lectureTitle || !content || !videoUrl || !duration) return alert("Vui lòng nhập đầy đủ các trường!");

  if (!lecturesPerModule[currentModuleIndex]) lecturesPerModule[currentModuleIndex] = [];
  const lessonIndex = lecturesPerModule[currentModuleIndex].length;
  lecturesPerModule[currentModuleIndex].push({ lectureTitle, content, videoUrl, duration: +duration || 0 });
  Storage.set("lecturesPerModule", lecturesPerModule);

  appendLessonToDOM(currentModuleIndex, lessonIndex);
  bootstrap.Modal.getInstance(document.getElementById("create-lesson")).hide();
}

function appendLessonToDOM(moduleIdx, lessonIdx) {
  const lesson = lecturesPerModule[moduleIdx][lessonIdx];
  const container = document.querySelector(`.lesson-container[data-module-index="${moduleIdx}"]`);
  const html = `
    <div class="lesson-item d-flex justify-content-between rbt-course-wrape mb-4">
      <div class="col-10 inner d-flex align-items-center gap-2">
        <i class="feather-menu cursor-scroll"></i>
        <h6 class="rbt-title mb-0">${lesson.lectureTitle}</h6>
      </div>
      <div class="col-2 inner">
        <ul class="rbt-list-style-1 rbt-course-list d-flex gap-2">
          <li><i class="feather-edit" data-bs-toggle="modal" data-bs-target="#update-lesson" onclick="editLesson(${moduleIdx}, ${lessonIdx}, this)"></i></li>
          <li><i class="feather-trash" onclick="deleteLesson(${moduleIdx}, ${lessonIdx}, this)"></i></li>
        </ul>
      </div>
    </div>
  `;
  container.insertAdjacentHTML("beforeend", html);
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

  bootstrap.Modal.getInstance(document.getElementById("update-lesson")).hide();
  currentEditingLesson = { moduleIndex: null, lessonIndex: null, domElement: null };
}
