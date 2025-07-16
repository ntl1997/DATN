document.addEventListener('DOMContentLoaded', function() {
    const rows = document.querySelectorAll('.email-row');
    const detailSubject = document.getElementById('detail-subject');
    const detailAvatar = document.getElementById('detail-avatar');
    const detailName = document.getElementById('detail-name');
    const detailEmail = document.getElementById('detail-email');
    const detailDate = document.getElementById('detail-date');
    const detailMessage = document.getElementById('detail-message');
    const detailPhone = document.getElementById('detail-phone');

    rows.forEach(function(row) {
        row.addEventListener('click', function() {
            detailSubject.textContent = "Liên hệ từ " + row.getAttribute('data-name');
            detailName.textContent = row.getAttribute('data-name');
            detailEmail.textContent = row.getAttribute('data-email');
            detailDate.textContent = row.getAttribute('data-date');
            detailMessage.textContent = row.getAttribute('data-message');
            detailPhone.textContent = "Số điện thoại: " + (row.getAttribute('data-phone') || "");
            // Nếu muốn đổi avatar theo từng người, có thể thêm data-avatar và set detailAvatar.src
            // Đánh dấu là đã đọc
            rows.forEach(r => r.classList.remove('read'));
            row.classList.remove('unread');
            row.classList.add('read');
        });
    });

    // Tự động hiển thị dòng đầu tiên khi load trang
    if (rows.length > 0) {
        rows[0].click();
    }
});
