$(document).ready(function() {
    // Xóa dữ liệu điền sẵn trên form (tránh việc trình duyệt tự điền hoặc HTML có sẵn value)
    // Sử dụng setTimeout để đảm bảo ghi đè cơ chế tự động điền trễ của trình duyệt
    setTimeout(function() {
        // Xóa form đăng nhập
        $('#email').val('');
        $('#password').val('');

        // Xóa form đăng ký
        $('#regFullName').val('');
        $('#regEmail').val('');
        $('#regPhone').val('');
        $('#regPassword').val('');
    }, 100);

    // --- JS FOR SLIDING ANIMATION ---
    const signUpButton = $('#signUp');
    const signInButton = $('#signIn');
    const container = $('#authContainer');

    signUpButton.on('click', () => {
        container.addClass('right-panel-active');
    });

    signInButton.on('click', () => {
        container.removeClass('right-panel-active');
    });

    // --- JS FOR LOGIN FORM SUBMISSION ---
    $('#loginForm').on('submit', function(e) {
        e.preventDefault();
        
        const btnLogin = $('#btnLogin');
        const originalText = btnLogin.text();
        btnLogin.prop('disabled', true).html('<i class="fas fa-spinner fa-spin me-2"></i>ĐANG XỬ LÝ...');
        
        const loginData = {
            email: $('#email').val().trim(),
            password: $('#password').val().trim()
        };

        $.ajax({
            url: '/api/users/login',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(loginData),
            success: function(response) {
                // Lưu thông tin user vào két sắt trình duyệt để các trang khác sử dụng
                localStorage.setItem('user_id', response.userId);
                localStorage.setItem('user_name', response.fullName);
                localStorage.setItem('user_email', response.email);
                localStorage.setItem('user_role', response.roleId);
                
                $('#loginAlert').removeClass('d-none alert-danger').addClass('alert-success').text('Đăng nhập thành công! Đang chuyển hướng...');
                
                setTimeout(function() {
                    // Nếu có phân quyền, chuyển hướng tới Admin hoặc Trang chủ tùy thuộc Role ID
                    window.location.href = response.roleId === 1 ? 'admin.html' : 'index.html';
                }, 1000);
            },
            error: function(xhr) {
                $('#loginAlert').removeClass('d-none alert-success').addClass('alert-danger').text(xhr.responseText || 'Email hoặc mật khẩu không chính xác!');
                btnLogin.prop('disabled', false).text(originalText);
            }
        });
    });

    // --- JS FOR REGISTER FORM SUBMISSION ---
    $("#registerForm").submit(function(e) {
        e.preventDefault();
        
        const payload = {
            fullName: $("#regFullName").val(),
            email: $("#regEmail").val(),
            phoneNumber: $("#regPhone").val(),
            password: $("#regPassword").val()
        };

        // Frontend Validation: Kiểm tra độ mạnh mật khẩu
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/;
        if (!passwordRegex.test(payload.password)) {
            const alertBox = $('#registerAlert');
            alertBox.removeClass('d-none alert-success').addClass('alert-danger')
                    .text("Mật khẩu phải có ít nhất 8 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt (@, #...)!");
            
            // Thêm hiệu ứng nhấp nháy để gây chú ý cho người dùng
            alertBox.fadeOut(100).fadeIn(100).fadeOut(100).fadeIn(100);
            return; // Dừng lại, không gọi API nếu mật khẩu yếu
        }

        const $btn = $(this).find('button[type="submit"]');
        const originalText = $btn.text();
        $btn.prop("disabled", true).html('<i class="fas fa-spinner fa-spin me-2"></i>ĐANG XỬ LÝ...');

        $.ajax({
            url: "/api/users/register",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(payload),
            success: function(response) {
                // Hiển thị thông báo đăng ký thành công
                $('#registerAlert').removeClass('d-none alert-danger').addClass('alert-success').text(response + ' Đang tự động chuyển sang đăng nhập...');
                
                // Trượt về trang đăng nhập sau 2 giây
                setTimeout(() => {
                    signInButton.click();
                }, 2000);
            },
            error: function(xhr) {
                $('#registerAlert').removeClass('d-none alert-success').addClass('alert-danger').text(xhr.responseText || "Đã xảy ra lỗi hệ thống!");
            },
            complete: function() {
                $btn.prop("disabled", false).text(originalText);
            }
        });
    });
});