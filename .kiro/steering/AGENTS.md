---
inclusion: always
---
<!------------------------------------------------------------------------------------
   Add rules to this file or a short description and have Kiro refine them for you.
   
   Learn about inclusion modes: https://kiro.dev/docs/steering/#inclusion-modes
-------------------------------------------------------------------------------------># Java–Quarkus–Camel–IBM MQ Guidelines for AI Agents (AI-DLC)

## Mục Đích

Tài liệu này định nghĩa **định hướng hoạt động bắt buộc cho AI Agent** khi tham gia vào vòng đời phát triển phần mềm (AI-DLC) của hệ thống Java sử dụng:

- Quarkus
- Apache Camel
- IBM MQ
- Kiến trúc microservice / message-driven

**AGENT trong tài liệu này là AI**, không phải microservice hay runtime component.

---

## Yêu Cầu Quan Trọng Cho AI

**BẮT BUỘC**: Khi AI tham gia phân tích, thiết kế, sinh code hoặc đưa ra khuyến nghị, AI PHẢI tuân thủ toàn bộ các nguyên tắc dưới đây.

---

## 1. Phạm Vi Hoạt Động Của AI

AI Agent được phép:
- Phân tích yêu cầu nghiệp vụ
- Đề xuất kiến trúc message-driven
- Sinh code Java theo chuẩn Quarkus
- Sinh Apache Camel routes
- Đề xuất cấu hình IBM MQ
- Đề xuất test strategy

AI Agent **KHÔNG ĐƯỢC**:
- Đề xuất framework ngoài phạm vi đã định
- Bịa API, thư viện hoặc pattern không tồn tại
- Giả định môi trường runtime không được mô tả
- Tự ý đơn giản hóa kiến trúc message-driven thành synchronous RPC

---

## 2. Công Nghệ BẮT BUỘC (Non-negotiable)

1. **Java + Quarkus**
   - AI **chỉ** được sinh code cho Quarkus
   - KHÔNG Spring Boot
   - KHÔNG Micronaut
   - KHÔNG Jakarta EE thuần

2. **Apache Camel**
   - Mọi xử lý message **phải** thông qua Camel
   - KHÔNG viết JMS consumer/producer thủ công
   - KHÔNG bypass Camel route

3. **IBM MQ**
   - IBM MQ là message broker duy nhất
   - KHÔNG Kafka, RabbitMQ, ActiveMQ
   - KHÔNG in-memory broker cho kiến trúc chính

---

## 3. Context7 Usage (BẮT BUỘC)

AI Agent **PHẢI sử dụng Context7** trước khi đưa ra bất kỳ quyết định kỹ thuật nào liên quan đến:

- Apache Camel component
- IBM MQ / JMS configuration
- Transaction (XA / non-XA)
- Retry, DLQ, redelivery
- Best practices

Nếu Context7 **không có thông tin rõ ràng**:
- AI phải **nói rõ mức độ không chắc chắn**
- KHÔNG được suy đoán hoặc bịa

---

## 4. Tư Duy Kiến Trúc BẮT BUỘC

AI Agent **phải tư duy theo các nguyên tắc sau**:

- Message-driven > Request-driven
- Asynchronous > Synchronous
- Loose coupling > Tight coupling
- Explicit flow > Magic abstraction
- Reliability > Convenience

AI **không được**:
- Đề xuất giao tiếp sync giữa các microservice
- Chuyển message flow thành REST call vì “đơn giản hơn”
- Thiết kế kiến trúc phụ thuộc thứ tự runtime

---

## 5. Message Communication Rules

### 5.1 Point-to-Point (Queue)

- Dùng cho:
  - Command
  - Xử lý nghiệp vụ
  - Request / Reply
- AI phải thiết kế:
  - Queue rõ domain
  - Có DLQ tương ứng

### 5.2 Publish / Subscribe (Topic)

- Dùng cho:
  - Event
  - Notification
- AI phải giả định:
  - Có nhiều consumer
  - Không phụ thuộc thứ tự xử lý

---

## 6. Request / Reply Pattern

Khi AI sinh giải pháp request/reply:

- **BẮT BUỘC** sử dụng:
  - `JMSReplyTo`
  - `JMSCorrelationID`
- Response phải giữ nguyên `JMSCorrelationID`

AI **KHÔNG ĐƯỢC**:
- Blocking chờ response
- Đề xuất synchronous RPC
- Dùng HTTP để giả lập reply

---

## 7. Apache Camel Design Rules

AI phải tuân thủ:

- Camel Route là orchestration layer
- Business logic nằm trong `Processor`
- Route phải rõ ràng, đọc được, không nhồi logic

AI phải luôn xem xét:
- `onException`
- Redelivery
- Dead Letter Queue

---

## 8. Error Handling & Reliability

AI Agent **BẮT BUỘC** phải:

- Thiết kế error flow
- Chỉ rõ message đi đâu khi lỗi
- Không “log rồi bỏ”

AI không được giả định:
- Message luôn đúng
- Consumer luôn thành công
- Network luôn ổn định

---

## 9. Configuration Philosophy

AI phải giả định:

- Không hardcode config
- Không hardcode credential
- Mọi thứ đều externalized

AI không được:
- Sinh giá trị giả cho secret
- Commit credential vào code

---

## 10. Testing Mindset

AI phải đề xuất test theo thứ tự ưu tiên:

1. Integration test (Camel + MQ)
2. End-to-end test
3. Unit test (business logic)

AI không được xem unit test là đủ cho message-driven system.

---

## 11. Greenfield vs Brownfield Awareness

### Greenfield
- Chưa có Camel route
- Chưa có MQ contract
- Chưa có message schema

→ AI bắt đầu từ **architecture + message contract**

### Brownfield
- Đã có route
- Đã có queue/topic
- Đã có message format

→ AI **phải phân tích trước khi đề xuất thay đổi**

---

## 12. Nguyên Tắc Hành Vi Cốt Lõi Cho AI

- Không đoán
- Không bịa
- Không tối ưu sớm
- Không phá kiến trúc hiện có
- Ưu tiên hệ thống chạy ổn định hơn là code “đẹp”

---

Tài liệu này là **ràng buộc hành vi cho AI Agent** trong AI-DLC.  
Mọi phân tích, khuyến nghị và code sinh ra **phải tuân thủ tài liệu này trước tiên**, sau đó mới đến yêu cầu nghiệp vụ.
