import { Github, Mail, Heart } from 'lucide-react';

const Footer = () => {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="py-12 border-t border-border">
      <div className="container mx-auto px-4">
        <div className="flex flex-col md:flex-row items-center justify-between gap-6">
          {/* Logo & Copyright */}
          <div className="text-center md:text-left">
            <h3 className="font-bold text-lg mb-1">Knock on Ports</h3>
            <p className="text-sm text-muted-foreground">
              © 2018-{currentYear} Alexander Yaburov. Licensed under Apache 2.0
            </p>
          </div>

          {/* Links */}
          <div className="flex items-center gap-6">
            <a
              href="mailto:dev@impa.me"
              className="flex items-center gap-2 text-muted-foreground hover:text-primary transition-colors text-sm"
            >
              <Mail className="h-4 w-4" />
              dev@impa.me
            </a>
            <a
              href="https://github.com/impalex/knockonports"
              target="_blank"
              rel="noopener noreferrer"
              className="flex items-center gap-2 text-muted-foreground hover:text-primary transition-colors text-sm"
            >
              <Github className="h-4 w-4" />
              GitHub
            </a>
          </div>
        </div>

        {/* Disclaimer */}
        <div className="mt-8 pt-6 border-t border-border">
          <p className="text-xs text-muted-foreground text-center max-w-3xl mx-auto">
            <strong>Disclaimer:</strong> This application is intended for legitimate network administration 
            and security testing purposes only. 
            The developer is not responsible for any misuse of this software. Always ensure you have proper 
            authorization before using port knocking on any network.
          </p>
        </div>

        {/* Made with love */}
        <div className="mt-6 flex items-center justify-center gap-1 text-xs text-muted-foreground">
          <span>Made with</span>
          <Heart className="h-3 w-3 text-destructive fill-destructive" />
          <span>for network professionals</span>
        </div>
      </div>
    </footer>
  );
};

export default Footer;