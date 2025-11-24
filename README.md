# Stream Board

Spring WebFlux 실습 목적으로 개발한 게시판 애플리케이션입니다. 사용된 기술은 다음과 같습니다.

- MySQL
- Spring Boot
- Spring Data R2DBC

# API 목록

## Post API

### 게시글 생성

- Method: `POST`
- URL: `/post`
- Request Body: `application/json`

```json
{
  "title": "string",
  "content": "string"
}
```

- Success Response:
    - Code: `201 Created`
    - Headers:
        - `Location`: `/post/{생성된_게시글_번호}`

---

### 게시글 목록 조회

게시글 목록을 페이지네이션으로 조회합니다. 커서 기반 페이지네이션을 사용합니다.

- Method: `GET`
- URL: `/post`
- Query Parameters:
    - `cursor` (optional, integer): 조회 시작 지점. 기본값은 `0`이며, 가장 최근 게시물부터 조회합니다. 이전 페이지의 마지막 게시물 `no`를 전달하여 다음 페이지를 조회할 수
      있습니다.
    - `size` (optional, integer): 페이지 당 게시물 수. 기본값은 `10`입니다.
- Success Response:
    - Code: `200 OK`
    - Response Body:

```json
{
  "code": "SUCCESS",
  "message": "요청이 성공적으로 처리되었습니다.",
  "result": [
    {
      "no": 1,
      "title": "첫 번째 게시글",
      "createdAt": "2025-11-24T10:00:00"
    },
    {
      "no": 2,
      "title": "두 번째 게시글",
      "createdAt": "2025-11-24T10:05:00"
    }
  ]
}
```

---

### 게시글 상세 조회

특정 게시글의 상세 내용을 조회합니다.

- Method: `GET`
- URL: `/post/{no}`
- Path Variable:
    - `no` (required, integer): 조회할 게시글의 번호
- Success Response:
    - Code: `200 OK`
    - Response Body:

```json
{
  "code": "SUCCESS",
  "message": "요청이 성공적으로 처리되었습니다.",
  "result": {
    "no": 1,
    "title": "첫 번째 게시글",
    "content": "게시글 내용입니다.",
    "createdAt": "2025-11-24T10:00:00"
  }
}

```

---

### 게시글 수정

특정 게시글의 제목 또는 내용을 수정합니다.

- Method: `PATCH`
- URL: `/post/{no}`
- Path Variable:
    - `no` (required, integer): 수정할 게시글의 번호
- Request Body: `application/json` (수정할 필드만 포함)

```json
{
  "title": "수정된 제목",
  "content": "수정된 내용"
}

```

- Success Response:
    - Code: `200 OK`
    - Response Body: (수정 후의 게시글 상세 정보)

```json
{
  "code": "SUCCESS",
  "message": "요청이 성공적으로 처리되었습니다.",
  "result": {
    "no": 1,
    "title": "수정된 제목",
    "content": "수정된 내용",
    "createdAt": "2025-11-24T10:00:00"
  }
}
```

---

### 게시글 삭제

특정 게시글을 삭제합니다.

- Method: `DELETE`
- URL: `/post/{no}`
- Path Variable:
    - `no` (required, integer): 삭제할 게시글의 번호
- Success Response:
    - Code: `204 No Content`