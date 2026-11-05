import { ServiceWorkerRegister } from "@/components/ServiceWorkerRegister";
import type { Metadata, Viewport } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Padroniza.ba - Gestão e Relatórios de Abate",
  description: "Painel de gestão, padronização e inteligência estratégica para relatórios de abate de bovinos.",
  manifest: "/manifest.json",
  openGraph: {
    title: "Padroniza.ba - Gestão e Relatórios de Abate",
    description: "Painel de gestão, padronização e inteligência estratégica para relatórios de abate de bovinos.",
  },
}

export const viewport: Viewport = {
  themeColor: "#c0392b",
}

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="pt-br">
      <body className="min-h-screen bg-background antialiased">
        {children}
        <ServiceWorkerRegister />
      </body>
    </html>
  );
}