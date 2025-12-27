import { ArrowRight, ExternalLink, Lock, Unlock } from 'lucide-react';
import { Button } from '@/components/ui/button';

const TechnicalSection = () => {
  return (
    <section className="py-24 bg-card/50">
      <div className="container mx-auto px-4">
        {/* Section header */}
        <div className="text-center mb-16">
          <span className="text-primary font-mono text-sm">// HOW IT WORKS</span>
          <h2 className="text-4xl md:text-5xl font-bold mt-2 mb-4">
            Port Knocking Explained
          </h2>
          <p className="text-muted-foreground text-lg max-w-2xl mx-auto">
            Port knocking is a stealth authentication method that opens firewall ports 
            only when the correct sequence of connection attempts is received.
          </p>
        </div>

        <div className="grid lg:grid-cols-2 gap-12 items-center">
          {/* Diagram */}
          <div className="space-y-6">
            <div className="bg-background rounded-xl border border-border p-6 md:p-8">
              {/* Step 1 */}
              <div className="flex items-start gap-4 mb-8">
                <div className="flex-shrink-0 w-10 h-10 rounded-full bg-destructive/20 flex items-center justify-center">
                  <Lock className="h-5 w-5 text-destructive" />
                </div>
                <div>
                  <h4 className="font-semibold mb-1">1. Service Hidden</h4>
                  <p className="text-sm text-muted-foreground">
                    SSH (port 22) is blocked by firewall. Port scans show nothing.
                  </p>
                  <div className="mt-3 font-mono text-xs bg-muted p-3 rounded">
                    <span className="text-muted-foreground">$ nmap -p 22 server.local</span>
                    <br />
                    <span className="text-destructive">22/tcp filtered ssh</span>
                  </div>
                </div>
              </div>

              {/* Arrow */}
              <div className="flex justify-center mb-8">
                <ArrowRight className="h-6 w-6 text-primary rotate-90" />
              </div>

              {/* Step 2 */}
              <div className="flex items-start gap-4 mb-8">
                <div className="flex-shrink-0 w-10 h-10 rounded-full bg-primary/20 flex items-center justify-center">
                  <span className="text-primary font-mono font-bold">K</span>
                </div>
                <div>
                  <h4 className="font-semibold mb-1">2. Knock Sequence Sent</h4>
                  <p className="text-sm text-muted-foreground">
                    Client sends packets to ports 7000 → 8000 → 9000 in order.
                  </p>
                  <div className="mt-3 font-mono text-xs bg-muted p-3 rounded space-y-1">
                    <div className="flex items-center gap-2">
                      <span className="text-terminal-cyan">UDP</span>
                      <ArrowRight className="h-3 w-3" />
                      <span>:7000</span>
                      <span className="text-terminal-green ml-auto">✓</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className="text-terminal-cyan">TCP</span>
                      <ArrowRight className="h-3 w-3" />
                      <span>:8000</span>
                      <span className="text-terminal-green ml-auto">✓</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className="text-terminal-cyan">TCP</span>
                      <ArrowRight className="h-3 w-3" />
                      <span>:9000</span>
                      <span className="text-terminal-green ml-auto">✓</span>
                    </div>
                  </div>
                </div>
              </div>

              {/* Arrow */}
              <div className="flex justify-center mb-8">
                <ArrowRight className="h-6 w-6 text-primary rotate-90" />
              </div>

              {/* Step 3 */}
              <div className="flex items-start gap-4">
                <div className="flex-shrink-0 w-10 h-10 rounded-full bg-terminal-green/20 flex items-center justify-center">
                  <Unlock className="h-5 w-5 text-terminal-green" />
                </div>
                <div>
                  <h4 className="font-semibold mb-1">3. Access Granted</h4>
                  <p className="text-sm text-muted-foreground">
                    Firewall opens SSH port for your IP. Service is now accessible.
                  </p>
                  <div className="mt-3 font-mono text-xs bg-muted p-3 rounded">
                    <span className="text-muted-foreground">$ ssh admin@server.local</span>
                    <br />
                    <span className="text-terminal-green">Connected to server.local</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Code example */}
          <div className="space-y-6">
            <div className="bg-background rounded-xl border border-border overflow-hidden">
              {/* Terminal header */}
              <div className="bg-muted px-4 py-3 flex items-center gap-2 border-b border-border">
                <div className="flex gap-1.5">
                  <div className="w-3 h-3 rounded-full bg-destructive/60" />
                  <div className="w-3 h-3 rounded-full bg-yellow-500/60" />
                  <div className="w-3 h-3 rounded-full bg-terminal-green/60" />
                </div>
                <span className="text-xs text-muted-foreground ml-2 font-mono">knockonports-config.json</span>
              </div>
              
              {/* Code content */}
<pre className="p-6 text-sm font-mono overflow-x-auto">
  <code className="text-foreground">
{`{
  `}<span className="text-primary">"name"</span>{`: `}<span className="text-terminal-green">"Office SSH"</span>{`,
  `}<span className="text-primary">"host"</span>{`: `}<span className="text-terminal-green">"vpn.office.com"</span>{`,
  `}<span className="text-primary">"delay"</span>{`: `}<span className="text-terminal-cyan">100</span>{`,
  `}<span className="text-primary">"ipv"</span>{`: `}<span className="text-terminal-green">"prefer_ipv4"</span>{`,
  `}<span className="text-primary">"application"</span>{`: `}<span className="text-terminal-green">"ssh://admin@vpn.office.com"</span>{`,
  `}<span className="text-primary">"steps"</span>{`: [
    { `}<span className="text-primary">"type"</span>{`: `}<span className="text-terminal-green">"udp"</span>{`, `}<span className="text-primary">"port"</span>{`: `}<span className="text-terminal-cyan">7000</span>{` },
    { `}<span className="text-primary">"type"</span>{`: `}<span className="text-terminal-green">"tcp"</span>{`, `}<span className="text-primary">"port"</span>{`: `}<span className="text-terminal-cyan">8000</span>{` },
    { `}<span className="text-primary">"type"</span>{`: `}<span className="text-terminal-green">"tcp"</span>{`, `}<span className="text-primary">"port"</span>{`: `}<span className="text-terminal-cyan">9000</span>{` }
  ]
}`}
  </code>
</pre>
            </div>

            <p className="text-sm text-muted-foreground">
              Configure your knock sequences easily with JSON. Export and share configurations 
              between devices, or version control them with your infrastructure code.
            </p>

            <div className="flex flex-wrap gap-4">
              <Button variant="outline" asChild>
                <a href="https://github.com/impalex/knockonports" target="_blank" rel="noopener noreferrer">
                  <ExternalLink className="h-4 w-4 mr-2" />
                  View Source Code
                </a>
              </Button>
              <Button variant="outline" asChild>
                <a href="https://github.com/impalex/knockonports/issues" target="_blank" rel="noopener noreferrer">
                  Report Issues
                </a>
              </Button>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default TechnicalSection;