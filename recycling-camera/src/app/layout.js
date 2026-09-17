import "./globals.css";

export const metadata = {
  title: "Recycling Scanner",
  description: "Identify an item and learn how to dispose of it."
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
