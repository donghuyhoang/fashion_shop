$(document).ready(function() {
    const API_URL = "/api/users/";
    const userId = localStorage.getItem("user_id");

    if (!userId || userId === "undefined") {
        alert("Vui lòng đăng nhập để xem hồ sơ!");
        window.location.href = "login.html";
        return;
    }

    // Lấy thông tin user hiện tại
    loadUserProfile();

    function loadUserProfile() {
        $.ajax({
            url: API_URL + userId,
            type: "GET",
            success: function(user) {
                if (user) {
                    $("#fullName").val(user.fullName);
                    $("#email").val(user.email);
                    $("#phone").val(user.phoneNumber);
                }
            },
            error: function(xhr) {
                console.error("Lỗi khi tải thông tin user:", xhr);
                showAlert("Không thể tải thông tin hồ sơ. Vui lòng thử lại sau.", "danger");
            }
        });
    }

    // Xử lý cập nhật hồ sơ
    $("#profileForm").submit(function(e) {
        e.preventDefault();
        
        const btnSubmit = $("#btnUpdateProfile");
        btnSubmit.prop("disabled", true).html('<i class="fas fa-spinner fa-spin me-2"></i> Đang lưu...');

        const updatedData = {
            userId: parseInt(userId),
            fullName: $("#fullName").val().trim(),
            email: $("#email").val().trim(),
            phoneNumber: $("#phone").val().trim()
        };

        $.ajax({
            url: API_URL + userId,
            type: "PUT",
            contentType: "application/json",
            data: JSON.stringify(updatedData),
            success: function(response) {
                showAlert("Cập nhật hồ sơ thành công!", "success");
                localStorage.setItem("user_name", updatedData.fullName); // Cập nhật lại tên trên Navbar
                $(".user-profile-toggle span").text(updatedData.fullName); // Cập nhật tên ngay lập tức ngoài màn hình
            },
            error: function() {
                showAlert("Có lỗi xảy ra khi cập nhật hồ sơ.", "danger");
            },
            complete: function() {
                btnSubmit.prop("disabled", false).html('<i class="fas fa-save me-2"></i> Lưu Thay Đổi');
            }
        });
    });

    // ==========================================
    // XỬ LÝ ĐỔI MẬT KHẨU
    // ==========================================
    $("#changePasswordForm").submit(function(e) {
        e.preventDefault();
        
        const oldPass = $("#oldPassword").val().trim();
        const newPass = $("#newPassword").val().trim();
        const confirmPass = $("#confirmPassword").val().trim();

        if (newPass !== confirmPass) {
            showModalAlert("Mật khẩu xác nhận không khớp!", "danger");
            return;
        }

        const btn = $("#btnSubmitPassword");
        btn.prop("disabled", true).html('<i class="fas fa-spinner fa-spin"></i> Đang xử lý...');

        $.ajax({
            url: API_URL + userId + "/change-password",
            type: "PUT",
            contentType: "application/json",
            data: JSON.stringify({ oldPassword: oldPass, newPassword: newPass }),
            success: function(res) {
                showModalAlert(res, "success");
                setTimeout(() => {
                    $("#changePasswordModal").modal('hide');
                    $("#changePasswordForm")[0].reset();
                    $("#modalAlertMessage").addClass("d-none");
                    
                    // Tự động xóa phiên đăng nhập cũ
                    localStorage.removeItem("user_name"); 
                    localStorage.removeItem("user_email"); 
                    localStorage.removeItem("user_token"); 
                    localStorage.removeItem("user_id"); 
                    
                    alert("Đổi mật khẩu thành công! Vui lòng đăng nhập lại để tiếp tục.");
                    window.location.href = "login.html"; // Chuyển về trang đăng nhập
                }, 1500);
            },
            error: function(xhr) {
                showModalAlert(xhr.responseText || "Đổi mật khẩu thất bại!", "danger");
            },
            complete: function() {
                btn.prop("disabled", false).text('Xác nhận đổi');
            }
        });
    });

    function showAlert(message, type) {
        const alertBox = $("#alertMessage");
        alertBox.removeClass("d-none alert-success alert-danger").addClass(`alert-${type}`).text(message);
        setTimeout(() => alertBox.addClass("d-none"), 3000);
    }

    function showModalAlert(message, type) {
        const alertBox = $("#modalAlertMessage");
        alertBox.removeClass("d-none alert-success alert-danger").addClass(`alert-${type}`).text(message);
    }
});