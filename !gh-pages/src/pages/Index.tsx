import { Helmet } from 'react-helmet-async';
import Navigation from '@/components/landing/Navigation';
import HeroSection from '@/components/landing/HeroSection';
import FeaturesTimeline from '@/components/landing/FeaturesTimeline';
import TechnicalSection from '@/components/landing/TechnicalSection';
import DownloadSection from '@/components/landing/DownloadSection';
import AboutSection from '@/components/landing/AboutSection';
import Footer from '@/components/landing/Footer';

const Index = () => {
  return (
    <>
      <Helmet>
        <title>Knock on Ports - Port Knocking Android App | Secure Network Access</title>
        <meta 
          name="description" 
          content="Open-source Android port knocking client for network engineers. Support for UDP, TCP, ICMP packets, IPv4/IPv6, custom payloads, and post-knock automation. Compatible with knockd and icmpKNOCK." 
        />
        <meta 
          name="keywords" 
          content="port knocking, Android app, network security, firewall, UDP, TCP, ICMP, IPv6, knockd, open source, network administration" 
        />
        <meta property="og:title" content="Knock on Ports - Port Knocking Android App" />
        <meta property="og:description" content="Securely unlock network access with precision port knocking. Open-source Android utility for network professionals." />
        <meta property="og:type" content="website" />
        <meta name="twitter:card" content="summary_large_image" />
        <meta name="twitter:title" content="Knock on Ports - Port Knocking Android App" />
        <meta name="twitter:description" content="Securely unlock network access with precision port knocking. Open-source Android utility for network professionals." />
        <link rel="canonical" href="https://impalex.github.io/knockonports/" />
      </Helmet>

      <div className="min-h-screen bg-background text-foreground">
        <Navigation />
        <main>
          <HeroSection />
          <FeaturesTimeline />
          <TechnicalSection />
          <DownloadSection />
          <AboutSection />
        </main>
        <Footer />
      </div>
    </>
  );
};

export default Index;