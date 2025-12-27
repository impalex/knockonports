import { Download, Star, Users, ExternalLink } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { useEffect, useState } from 'react';

const stores = [
  {
    name: 'Google Play',
    url: 'https://play.google.com/store/apps/details?id=me.impa.knockonports',
    icon: '/google-play.svg',
    description: 'Official Google Play Store',
    primary: true,
  },
  {
    name: 'F-Droid',
    url: 'https://f-droid.org/packages/me.impa.knockonports/',
    icon: '/f-droid.svg',
    description: 'Free and Open Source Repository',
    primary: false,
  },
  {
    name: 'GitHub Releases',
    url: 'https://github.com/impalex/knockonports/releases',
    icon: '/github.svg',
    description: 'Direct APK Download',
    primary: false,
  },
  {
    name: 'RuStore',
    url: 'https://www.rustore.ru/catalog/app/me.impa.knockonports',
    icon: '/rustore.svg',
    description: 'Russian App Store',
    primary: false,
  },
];

const DownloadSection = () => {
  const [latestVersion, setLatestVersion] = useState('0.0.0');
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchLatestRelease = async () => {
      try {
        const response = await fetch(
          'https://api.github.com/repos/impalex/knockonports/releases/latest'
        );
        const data = await response.json();
        if (data.tag_name) {
          setLatestVersion(data.tag_name);
        }
      } catch (error) {
        console.error('Failed to fetch latest release:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchLatestRelease();
  }, []);	
	
  return (
    <section id="download" className="py-24">
      <div className="container mx-auto px-4">
        {/* Section header */}
        <div className="text-center mb-16">
          <span className="text-primary font-mono text-sm">// DOWNLOAD</span>
          <h2 className="text-4xl md:text-5xl font-bold mt-2 mb-4">
            Get Knock on Ports
          </h2>
          <p className="text-muted-foreground text-lg max-w-2xl mx-auto">
            Available on multiple platforms. Choose your preferred source.
          </p>
          
          {/* Version badge */}
          <div className="inline-flex items-center gap-2 mt-4 px-4 py-2 rounded-full bg-secondary border border-border">
            <span className="text-sm font-mono">v{isLoading ? 'Loading...' : latestVersion}</span>
            <span className="text-muted-foreground">•</span>
            <span className="text-sm text-muted-foreground">Latest Release</span>
          </div>
        </div>

        {/* Download cards */}
        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4 max-w-5xl mx-auto">
          {stores.map((store, index) => (
            <a
              key={index}
              href={store.url}
              target="_blank"
              rel="noopener noreferrer"
              className={`
                group relative flex flex-col items-center p-6 rounded-xl border transition-all duration-300
                ${store.primary 
                  ? 'bg-primary/10 border-primary/50 hover:border-primary hover:bg-primary/20' 
                  : 'bg-card border-border hover:border-primary/50 hover:bg-card/80'
                }
              `}
            >
              {/* Store icon placeholder */}
              <div className="w-12 h-12 mb-4 rounded-lg bg-background flex items-center justify-center border border-border">
                <Download className="h-6 w-6 text-primary" />
              </div>
              
              <h3 className="font-semibold mb-1 group-hover:text-primary transition-colors">
                {store.name}
              </h3>
              <p className="text-xs text-muted-foreground text-center">
                {store.description}
              </p>
              
              <ExternalLink className="absolute top-4 right-4 h-4 w-4 text-muted-foreground opacity-0 group-hover:opacity-100 transition-opacity" />
            </a>
          ))}
        </div>

        {/* Requirements note */}
        <p className="text-center text-sm text-muted-foreground mt-8">
          Requires Android 7.0 or higher.
        </p>
      </div>
    </section>
  );
};

export default DownloadSection;