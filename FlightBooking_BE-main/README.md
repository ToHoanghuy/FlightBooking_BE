# Hệ Thống Quản Lý Đặt Vé Máy Bay 🛫📊

## Mô tả
Hệ thống quản lý đặt vé máy bay là một ứng dụng web được thiết kế để đơn giản hóa quá trình đặt vé máy bay. Nó cung cấp một giao diện thân thiện với người dùng cho khách hàng tìm kiếm chuyến bay, đặt vé và quản lý đặt chỗ của họ. Ngoài ra, các quản trị viên có thể quản lý lịch trình chuyến bay, xem chi tiết đặt chỗ và thực hiện các nhiệm vụ quản trị khác thông qua bảng điều khiển dành riêng.

## Công nghệ Sử Dụng
- ![Java](https://img.shields.io/badge/Java-%23ED8B00.svg?&style=for-the-badge&logo=java&logoColor=white)
- ![Spring Boot](https://img.shields.io/badge/Spring_Boot-%236DB33F.svg?&style=for-the-badge&logo=spring&logoColor=white)
- ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-%23316192.svg?&style=for-the-badge&logo=postgresql&logoColor=white)

## Tính Năng
- Xác thực và ủy quyền người dùng
- Chức năng tìm kiếm và đặt vé chuyến bay
- Bảng điều khiển quản trị cho việc quản lý chuyến bay và đặt vé
- Quản lý hồ sơ khách hàng

## Cài Đặt và Thiết Lập
Để chạy Hệ Thống Quản Lý Đặt Vé Máy Bay trên máy cục bộ, làm theo các bước sau:

1. Sao chép kho lưu trữ:
   ```bash
   git clone https://github.com/LeDangThuong/FlightBooking_BE.git
2. Di chuyển đến thư mục dự án:
   ```bash
   cd FlightBooking_BE
3. Đảm bảo rằng JDK 17 được cài đặt trên máy của bạn. Đồng thời, hãy đảm bảo Gradle 8.5 và PostgreSQL đã được cài đặt.

4. Thiết lập cơ sở dữ liệu PostgreSQL:
   - Tạo một cơ sở dữ liệu PostgreSQL mới cho ứng dụng.
   - Cập nhật cấu hình cơ sở dữ liệu trong application.properties với thông tin đăng nhập của cơ sở dữ liệu của bạn.
5. Chạy ứng dụng bằng Gradle:
   ```bash
   ./gradlew bootRun
6. Truy cập ứng dụng tại http://localhost:8080 trong trình duyệt web của bạn.

## Hướng Dẫn Đóng Góp
Chúng tôi hoan nghênh sự đóng góp từ cộng đồng. Nếu bạn muốn đóng góp vào dự án, vui lòng tuân thủ các hướng dẫn sau:

- Fork kho lưu trữ
- Tạo nhánh tính năng của bạn (git checkout -b feature/YourFeature)
- Commit các thay đổi (git commit -m 'Thêm một số tính năng')
- Push lên nhánh (git push origin feature/YourFeature)
- Tạo Pull Request mới

## Giấy Phép
Dự án này được cấp phép theo Giấy Phép MIT. Xem tập tin LICENSE để biết chi tiết.

## Thông Tin Bổ Sung
Để biết thêm chi tiết hoặc yêu cầu, vui lòng liên hệ Lê Đăng Thương qua email.

## Server 
https://flightbookingbe-production.up.railway.app/swagger-ui/index.html