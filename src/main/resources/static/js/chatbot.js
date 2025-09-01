document.addEventListener("DOMContentLoaded", function () {
  const chatbotToggle = document.getElementById("chatbotToggle");
  const chatbot = document.getElementById("chatbot");
  const chatbotClose = document.getElementById("chatbotClose");
  const chatbotInput = document.getElementById("chatbotInput");
  const chatbotSend = document.getElementById("chatbotSend");
  const chatbotMessages = document.getElementById("chatbotMessages");

  // Add CSS file for chatbot
  const chatbotStyles = document.createElement("link");
  chatbotStyles.rel = "stylesheet";
  chatbotStyles.href = "/static/admin-assets/css/chatbot.css";
  document.head.appendChild(chatbotStyles);

  chatbotToggle.addEventListener("click", () => {
    chatbot.classList.toggle("hidden");
  });

  chatbotClose.addEventListener("click", () => {
    chatbot.classList.add("hidden");
  });

  function sendMessage() {
    const userMessage = chatbotInput.value.trim();
    if (!userMessage) return;

    const userBubble = document.createElement("div");
    userBubble.className = "chatbot-message user";
    userBubble.textContent = userMessage;
    chatbotMessages.appendChild(userBubble);

    chatbotInput.value = "";

    try {
      console.log("Đang gửi yêu cầu đến API với nội dung:", userMessage);
      fetch("/api/chatbot", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ message: userMessage }),
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error(`API trả về lỗi: ${response.status}`);
          }
          return response.json();
        })
        .then((data) => {
          const botBubble = document.createElement("div");
          botBubble.className = "chatbot-message bot";
          botBubble.textContent =
            data.reply || "Xin lỗi, tôi không thể trả lời câu hỏi này.";
          chatbotMessages.appendChild(botBubble);
        })
        .catch((error) => {
          console.error("Lỗi khi gọi API chatbot:", error);
          const errorBubble = document.createElement("div");
          errorBubble.className = "chatbot-message bot";
          errorBubble.textContent = "Có lỗi xảy ra, vui lòng thử lại sau.";
          chatbotMessages.appendChild(errorBubble);
        });
    } catch (error) {
      console.error("Lỗi khi gọi API chatbot:", error);
    }
  }

  chatbotSend.addEventListener("click", sendMessage);

  chatbotInput.addEventListener("keypress", (event) => {
    if (event.key === "Enter") {
      sendMessage();
    }
  });
});
