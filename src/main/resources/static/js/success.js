$(document).ready(function() {
    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get('orderId');
    const method = urlParams.get('method');

    if (orderId) {
        $('#displayOrderId').text('#SP-' + orderId);
    }
    
    if (method === 'VNPAY' || method === 'vnpay') {
        $('#displayMethod').text('Thanh toán qua VNPay');
        $('#vnpayMessage').removeClass('d-none');
    } else if (method === 'MOMO') {
        $('#displayMethod').text('Thanh toán qua ví MoMo');
        $('#vnpayMessage').html('<i class="fas fa-info-circle me-2"></i> <strong>Cổng thanh toán MoMo:</strong> Hệ thống đang chuyển hướng bạn sang ứng dụng MoMo để quét mã...').removeClass('d-none');
    } else if (method === 'BANKING') {
        $('#displayMethod').text('Chuyển khoản ngân hàng');
    } else { // Mặc định là COD
        $('#displayMethod').text('Thanh toán khi nhận hàng (COD)');
    }
});