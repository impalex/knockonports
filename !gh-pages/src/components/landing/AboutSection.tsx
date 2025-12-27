import { useState } from 'react';
import { Heart, Scale, Copy, Check, ExternalLink } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { toast } from '@/hooks/use-toast';

const AboutSection = () => {
  const [copied, setCopied] = useState(false);
  const ethAddress = '0x8848210AD76bEA6BD040BAFd931558a1372Bf0D7';

  const copyAddress = async () => {
    await navigator.clipboard.writeText(ethAddress);
    setCopied(true);
    toast({
      title: 'Address copied',
      description: 'Ethereum address copied to clipboard',
    });
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <section className="py-24 bg-card/50">
      <div className="container mx-auto px-4">
        <div className="max-w-4xl mx-auto">
          {/* Section header */}
          <div className="text-center mb-12">
            <span className="text-primary font-mono text-sm">// ABOUT</span>
            <h2 className="text-4xl md:text-5xl font-bold mt-2 mb-4">
              Open Source & Privacy-First
            </h2>
          </div>

          <div className="grid md:grid-cols-2 gap-8">
            {/* Developer & License */}
            <div className="bg-background rounded-xl border border-border p-6">
              <div className="flex items-center gap-3 mb-4">
                <Scale className="h-6 w-6 text-primary" />
                <h3 className="text-xl font-semibold">License & Credits</h3>
              </div>
              
              <div className="space-y-4">
                <div>
                  <span className="text-sm text-muted-foreground">Developer</span>
                  <p className="font-medium">Alexander Yaburov</p>
                </div>
                
                <div>
                  <span className="text-sm text-muted-foreground">License</span>
                  <p className="font-medium">Apache License 2.0</p>
                  <p className="text-sm text-muted-foreground mt-1">
                    Free to use, modify, and distribute. Contributions welcome!
                  </p>
                </div>

                <div>
                  <span className="text-sm text-muted-foreground">Privacy</span>
                  <ul className="text-sm space-y-1 mt-1">
                    <li className="flex items-center gap-2">
                      <span className="text-terminal-green">✓</span>
                      No data collection
                    </li>
                    <li className="flex items-center gap-2">
                      <span className="text-terminal-green">✓</span>
                      No analytics or tracking
                    </li>
                    <li className="flex items-center gap-2">
                      <span className="text-terminal-green">✓</span>
                      No advertisements
                    </li>
                  </ul>
                </div>
              </div>
            </div>

            {/* Donations */}
            <div className="bg-background rounded-xl border border-border p-6">
              <div className="flex items-center gap-3 mb-4">
                <Heart className="h-6 w-6 text-primary" />
                <h3 className="text-xl font-semibold">Support Development</h3>
              </div>
              
              <p className="text-muted-foreground mb-6">
                If you find this app useful, consider supporting its continued development.
              </p>

              <div className="space-y-4">
                {/* Ethereum */}
                <div>
                  <span className="text-sm text-muted-foreground">Ethereum (ETH)</span>
                  <div className="flex items-center gap-2 mt-2">
                    <code className="flex-1 text-xs font-mono bg-muted p-3 rounded border border-border truncate">
                      {ethAddress}
                    </code>
                    <Button
                      variant="outline"
                      size="icon"
                      onClick={copyAddress}
                      className="flex-shrink-0"
                    >
                      {copied ? (
                        <Check className="h-4 w-4 text-terminal-green" />
                      ) : (
                        <Copy className="h-4 w-4" />
                      )}
                    </Button>
                  </div>
                </div>

                {/* CloudTips */}
                <div>
                  <span className="text-sm text-muted-foreground">CloudTips</span>
                  <Button
                    variant="outline"
                    className="w-full mt-2 justify-between"
                    asChild
                  >
                    <a 
                      href="https://pay.cloudtips.ru/p/57c93be7" 
                      target="_blank" 
                      rel="noopener noreferrer"
                    >
                      Donate via CloudTips
                      <ExternalLink className="h-4 w-4" />
                    </a>
                  </Button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default AboutSection;