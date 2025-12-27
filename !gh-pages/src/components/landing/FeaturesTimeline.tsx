import { useEffect, useRef, useState } from 'react';
import { 
  Network, 
  Globe, 
  Rocket, 
  Zap, 
  Palette, 
  Database, 
  Shield,
  Terminal
} from 'lucide-react';

const features = [
  {
    icon: Network,
    title: 'Protocol Flexibility',
    description: 'Support for UDP, TCP, and ICMP packets with full customization capabilities.',
    details: [
      'Custom port sequences with configurable order',
      'Inter-packet delays',
      'TTL/Hop Limit configuration',
      'Custom payload support (hex/ASCII)',
    ],
    code: `{
  "ports": [7000, 8000, 9000],
  "protocol": "UDP",
  "delay": 100,
  "ttl": 64
}`,
  },
  {
    icon: Globe,
    title: 'Dual-Stack Networking',
    description: 'Full IPv4 and IPv6 compatibility for modern network environments.',
    details: [
      'Seamless IPv4/IPv6 switching',
      'DNS resolution for hostnames',
      'Per-sequence IP stack selection',
    ],
    code: `target: "192.168.1.1"
target: "fe80::1%eth0"
target: "secure.example.com"`,
  },
  {
    icon: Rocket,
    title: 'Automation Tools',
    description: 'Launch applications or URLs automatically after successful knock sequences.',
    details: [
      'Post-knock app launching (SSH clients, VPNs)',
      'URL/URI scheme integration',
      'Resource availability verification (URL, TCP port, or ping)',
      'Automatic retry knocking on access failure',
    ],
    code: `onSuccess: {
  launch: "ssh://admin@server"
}`,
  },
  {
    icon: Zap,
    title: 'Quick Execution',
    description: 'One-tap sequence triggering from your home screen.',
    details: [
      'Home screen shortcuts',
      'Widgets'
    ],
    code: `$ adb shell am start \\
  -a android.intent.action.VIEW  \\
  -d "knockonports://knock/office"`,
  },
  {
    icon: Palette,
    title: 'Customization',
    description: 'Personalize the app appearance to match your preferences.',
    details: [
      'Dynamic Material You themes (Android 12+)',
      'Light and dark mode support',
      'High contrast accessibility mode',
      'Custom accent colors',
    ],
    code: `theme: "dynamic" | "dark" | "light"
contrast: "normal" | "high"`,
  },
  {
    icon: Database,
    title: 'Data Management',
    description: 'Never lose your configurations with robust backup options.',
    details: [
      'Export configurations to JSON',
      'Import from backup files',
    ],
    code: `$ knockonports --export config.json
$ knockonports --import backup.json`,
  },
  {
    icon: Shield,
    title: 'Security First',
    description: 'Your security is our priority. No ads, no tracking, fully open-source.',
    details: [
      'Biometric authentication (fingerprint/face)',
      'PIN/pattern lock option',
      'No data collection or analytics',
      'Apache 2.0 open-source license',
    ],
    code: `security: {
  lock: "biometric",
  timeout: 300,
  tracking: false
}`,
  },
];

const TimelineItem = ({ 
  feature, 
  index, 
  isVisible 
}: { 
  feature: typeof features[0]; 
  index: number; 
  isVisible: boolean;
}) => {
  const Icon = feature.icon;
  const isLeft = index % 2 === 0;

  return (
    <div className={`flex items-center gap-4 md:gap-8 ${isLeft ? 'md:flex-row' : 'md:flex-row-reverse'}`}>
      {/* Content */}
      <div 
        className={`flex-1 ${isLeft ? 'md:text-right' : 'md:text-left'}`}
        style={{
          opacity: isVisible ? 1 : 0,
          transform: isVisible 
            ? 'translateX(0)' 
            : `translateX(${isLeft ? '-30px' : '30px'})`,
          transition: 'all 0.6s ease-out',
        }}
      >
        <div className={`bg-card border border-border rounded-lg p-6 ${isLeft ? 'md:ml-auto' : 'md:mr-auto'} max-w-xl`}>
          <div className={`flex items-center gap-3 mb-4 ${isLeft ? 'md:flex-row-reverse' : ''}`}>
            <div className="p-2 rounded-lg bg-primary/10 text-primary">
              <Icon className="h-6 w-6" />
            </div>
            <h3 className="text-xl font-semibold">{feature.title}</h3>
          </div>
          
          <p className="text-muted-foreground mb-4">{feature.description}</p>
          
          <ul className={`space-y-2 mb-4 ${isLeft ? 'md:text-right' : ''}`}>
            {feature.details.map((detail, i) => (
              <li key={i} className="text-sm text-muted-foreground flex items-center gap-2">
                <span className="text-terminal-green">›</span>
                {detail}
              </li>
            ))}
          </ul>
          
          {/* Code snippet */}
          <div className="bg-background rounded-md p-3 border border-border">
            <div className="flex items-center gap-2 mb-2">
              <Terminal className="h-3 w-3 text-muted-foreground" />
              <span className="text-xs text-muted-foreground font-mono">config</span>
            </div>
            <pre className="text-xs font-mono text-terminal-cyan overflow-x-auto">
              {feature.code}
            </pre>
          </div>
        </div>
      </div>
      
      {/* Timeline node */}
      <div 
        className="hidden md:flex flex-col items-center"
        style={{
          opacity: isVisible ? 1 : 0,
          transform: isVisible ? 'scale(1)' : 'scale(0.5)',
          transition: 'all 0.4s ease-out',
        }}
      >
        <div className="w-4 h-4 rounded-full bg-primary glow" />
      </div>
      
      {/* Spacer for alternating layout */}
      <div className="hidden md:block flex-1" />
    </div>
  );
};

const FeaturesTimeline = () => {
  const [visibleItems, setVisibleItems] = useState<Set<number>>(new Set());
  const itemRefs = useRef<(HTMLDivElement | null)[]>([]);

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          const index = Number(entry.target.getAttribute('data-index'));
          if (entry.isIntersecting) {
            setVisibleItems((prev) => new Set([...prev, index]));
          }
        });
      },
      { threshold: 0.2, rootMargin: '-50px' }
    );

    itemRefs.current.forEach((ref) => {
      if (ref) observer.observe(ref);
    });

    return () => observer.disconnect();
  }, []);

  return (
    <section id="features" className="py-24 relative">
      <div className="container mx-auto px-4">
        {/* Section header */}
        <div className="text-center mb-16">
          <span className="text-primary font-mono text-sm">// FEATURES</span>
          <h2 className="text-4xl md:text-5xl font-bold mt-2 mb-4">
            Built for Professionals
          </h2>
          <p className="text-muted-foreground text-lg max-w-2xl mx-auto">
            Every feature designed with network engineers and security specialists in mind. 
            Compatible with knockd, icmpKNOCK, and custom implementations.
          </p>
        </div>

        {/* Timeline */}
        <div className="relative">
          {/* Vertical line */}
          <div className="hidden md:block absolute left-1/2 top-0 bottom-0 w-px timeline-line -translate-x-1/2" />
          
          <div className="space-y-12 md:space-y-24">
            {features.map((feature, index) => (
              <div
                key={index}
                ref={(el) => (itemRefs.current[index] = el)}
                data-index={index}
              >
                <TimelineItem 
                  feature={feature} 
                  index={index} 
                  isVisible={visibleItems.has(index)}
                />
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  );
};

export default FeaturesTimeline;