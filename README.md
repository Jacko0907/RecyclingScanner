# Recycling Scanner Starter

A mobile-friendly starter app using Next.js and Java Spring Boot.

## What it does

- Opens the phone's rear camera or photo picker
- Previews the selected image
- Uploads the image to the Java backend
- Returns and displays a sample recycling classification

The backend currently returns a mock result. Replace the marked section in
`recycling-api/src/main/java/com/example/recyclingapi/controller/ItemController.java`
with your chosen image-recognition service later.

## Requirements

- Node.js 20 or newer
- Java 21
- Maven 3.9 or newer (or use an IDE such as IntelliJ)

## 1. Run the Java backend

```bash
cd recycling-api
mvn spring-boot:run
```

Test it at <http://localhost:8080/api/test>.

## 2. Run the Next.js frontend

Open a second terminal:

```bash
cd recycling-camera
npm install
npm run dev
```

Open <http://localhost:3000>.

## Testing on a phone

For basic desktop testing, use localhost. To test the camera on a physical
phone, the page normally needs HTTPS and the phone must be able to reach both
the frontend and backend. A deployment or secure development tunnel is the
easiest option.

