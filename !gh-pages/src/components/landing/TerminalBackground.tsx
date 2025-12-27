import { useEffect, useState } from 'react';

const codeSnippets = [
  'knock -v 192.168.1.1 7000:udp 8000:tcp 9000:tcp',
  '$ ssh admin@secure-server.local',
  'iptables -A INPUT -m state --state NEW -m tcp -p tcp --dport 22 -j ACCEPT',
  'sequence: [7000, 8000, 9000]',
  'protocol: UDP | TCP | ICMP',
  'timeout: 5000ms',
  '# Port knock sequence completed',
  'Connection established...',
  'payload: 0x4B4E4F434B',
  'ttl: 64, delay: 100ms',
  '$ knockd -d -c /etc/knockd.conf',
  'IPv6: fe80::1%eth0',
];

const TerminalBackground = () => {
  const [floatingCode, setFloatingCode] = useState<Array<{
    id: number;
    text: string;
    x: number;
    y: number;
    opacity: number;
    delay: number;
  }>>([]);

  useEffect(() => {
    const generateCode = () => {
      const codes = [];
      for (let i = 0; i < 15; i++) {
        codes.push({
          id: i,
          text: codeSnippets[Math.floor(Math.random() * codeSnippets.length)],
          x: Math.random() * 100,
          y: Math.random() * 100,
          opacity: 0.03 + Math.random() * 0.07,
          delay: Math.random() * 10,
        });
      }
      setFloatingCode(codes);
    };

    generateCode();
  }, []);

  return (
    <div className="absolute inset-0 overflow-hidden">
      {/* Gradient overlay */}
      <div className="absolute inset-0 bg-gradient-to-b from-background via-background/95 to-background" />
      
      {/* Grid pattern */}
      <div 
        className="absolute inset-0 opacity-[0.02]"
        style={{
          backgroundImage: `
            linear-gradient(hsl(var(--primary)) 1px, transparent 1px),
            linear-gradient(90deg, hsl(var(--primary)) 1px, transparent 1px)
          `,
          backgroundSize: '50px 50px',
        }}
      />
      
      {/* Floating code snippets */}
      {floatingCode.map((code) => (
        <div
          key={code.id}
          className="absolute font-mono text-xs sm:text-sm text-primary whitespace-nowrap float-code select-none pointer-events-none"
          style={{
            left: `${code.x}%`,
            top: `${code.y}%`,
            opacity: code.opacity,
            animationDelay: `${code.delay}s`,
          }}
        >
          {code.text}
        </div>
      ))}
      
      {/* Radial glow from center */}
      <div className="absolute inset-0 bg-[radial-gradient(ellipse_at_center,hsl(var(--primary)/0.1)_0%,transparent_70%)]" />
      
      {/* Scan lines effect */}
      <div className="absolute inset-0 scanlines" />
    </div>
  );
};

export default TerminalBackground;