import { ArrowDown, Github, Download } from 'lucide-react';
import { Button } from '@/components/ui/button';
import TerminalBackground from './TerminalBackground';

const HeroSection = () => {
  return (
    <section className="relative min-h-screen flex items-center justify-center overflow-hidden">
      <TerminalBackground />
      
      {/* Content */}
      <div className="relative z-10 container mx-auto px-4 py-20 text-center">
        <div className="max-w-4xl mx-auto space-y-8">
          {/* Terminal prompt style badge */}
          <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-secondary/50 border border-border backdrop-blur-sm animate-fade-in">
            <span className="text-terminal-green font-mono text-sm">$</span>
            <span className="text-muted-foreground font-mono text-sm">open-source • apache-2.0</span>
          </div>

          {/* App name with glow */}
          <h1 
            className="text-5xl md:text-7xl lg:text-8xl font-bold tracking-tight glow-text animate-fade-in"
            style={{ animationDelay: '0.1s' }}
          >
            Knock on Ports
          </h1>

          {/* Tagline */}
          <p 
            className="text-xl md:text-2xl lg:text-3xl text-primary font-medium animate-fade-in"
            style={{ animationDelay: '0.2s' }}
          >
            Securely Unlock Network Access with Precision Port Knocking
          </p>

          {/* Description */}
          <p 
            className="text-muted-foreground text-lg md:text-xl max-w-3xl mx-auto leading-relaxed animate-fade-in"
            style={{ animationDelay: '0.3s' }}
          >
            An open-source Android utility for executing custom{' '}
            <span className="text-terminal-cyan font-mono">UDP</span>,{' '}
            <span className="text-terminal-cyan font-mono">TCP</span>, or{' '}
            <span className="text-terminal-cyan font-mono">ICMP</span>{' '}
            knocking sequences to discreetly open firewall-protected services. 
            Supports IPv4/IPv6, packet customization, and post-knock automation.
          </p>

          {/* CTA Buttons */}
          <div 
            className="flex flex-col sm:flex-row gap-4 justify-center pt-4 animate-fade-in"
            style={{ animationDelay: '0.4s' }}
          >
            <Button 
              size="lg" 
              className="glow text-lg px-8 py-6 font-semibold"
              onClick={() => document.getElementById('download')?.scrollIntoView({ behavior: 'smooth' })}
            >
              <Download className="mr-2 h-5 w-5" />
              Download Now
            </Button>
            <Button 
              variant="outline" 
              size="lg" 
              className="text-lg px-8 py-6 border-primary/50 hover:bg-primary/10"
              asChild
            >
              <a href="https://github.com/impalex/knockonports" target="_blank" rel="noopener noreferrer">
                <Github className="mr-2 h-5 w-5" />
                View on GitHub
              </a>
            </Button>
          </div>

          {/* Scroll indicator */}
          <div 
            className="pt-12 animate-fade-in"
            style={{ animationDelay: '0.5s' }}
          >
            <button 
              onClick={() => document.getElementById('features')?.scrollIntoView({ behavior: 'smooth' })}
              className="inline-flex flex-col items-center gap-2 text-muted-foreground hover:text-primary transition-colors"
            >
              <span className="text-sm font-mono">scroll to explore</span>
              <ArrowDown className="h-5 w-5 animate-float" />
            </button>
          </div>
        </div>
      </div>
    </section>
  );
};

export default HeroSection;