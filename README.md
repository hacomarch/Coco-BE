
![메인페이지(로그인X)-라이트모드](https://github.com/hacomarch/Coco-BE/assets/64404604/2623162a-e3f8-4e49-b026-8a432f2bdf04)

# WebIDE - 온라인 코드 편집기

## 개요
WebIDE는 사용자가 웹 브라우저에서 코드 작성, 실행 및 프로젝트 관리를 할 수 있게 도와주는 온라인 코드 편집기입니다.

<br>

## 진행 일정
2024.04.22 - 2024.05.21

<br>

## 팀원
김선희, 윤하은, 이소은, 천승환

<br>

## 사용 스택

<img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=SpringBoot&logoColor=white"> <img src="https://img.shields.io/badge/SpringSecurity-6DB33F?style=for-the-badge&logo=SpringSecurity&logoColor=white"> <img src="https://img.shields.io/badge/JPA-6DB33F?style=for-the-badge&logo=&logoColor=white">

<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"> <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white">


<img src="https://img.shields.io/badge/Amazon%20EC2-FF9900?style=for-the-badge&logo=AmazonEC2&logoColor=white"> <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white">

<br>

## ERD

<img width="1082" alt="image" src="https://github.com/hacomarch/Coco-BE/assets/64404604/1afadfee-7a1c-402a-9cc1-77b8ba3975a1">

<br>

## 기여한 기능

### 1. 코드 실행 기능
IDE 내에서 사용자가 작성한 코드를 실행할 수 있는 기능을 구현했습니다. 다양한 프로그래밍 언어를 지원하며, 코드를 실행한 결과를 확인할 수 있습니다.
- **언어 지원**: Python, JavaScript, Java 등
- **실행 방법**: 사용자가 코드 창에 코드를 입력하고 '저장' 버튼을 누른 후, '실행' 버튼을 누르면 결과가 출력 창에 표시됩니다.
- **구현 방법**: 파일 ID를 이용해 파일을 조회합니다. 지원하는 프로그래밍 언어에 따라 Docker 명령어를 구성합니다. `ProcessBuilder`를 사용하여 Docker 컨테이너에서 코드를 실행합니다. 실행 결과와 에러 메시지를 각각 처리하고 반환합니다.
### 2. 프로젝트, 폴더, 파일 CRUD 기능
프로젝트, 폴더, 파일을 생성, 읽기, 수정, 삭제할 수 있는 기능을 구현했습니다.

![IDE페이지-다크모드](https://github.com/hacomarch/Coco-BE/assets/64404604/438df8a8-6be3-4dd9-99f6-da15c5a4e911)

<br>

## 시연 동영상
https://github.com/hacomarch/Coco-BE/assets/64404604/ffe9e3e7-11b5-4022-8eb3-f6cc983312d8
