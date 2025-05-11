{
  description = "Dev environment for ANTLR with Java";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs = { nixpkgs, ... }:
    let
      pkgs = nixpkgs.legacyPackages.x86_64-linux;
    in {
      devShells.x86_64-linux.default = pkgs.mkShell {
        name = "antlr-dev";

        buildInputs = [
          pkgs.openjdk17  # Java 17
          pkgs.antlr4      # ANTLR4 CLI
          pkgs.maven
        ];

        shellHook = ''
          export CLASSPATH=".:${pkgs.antlr4}/lib/antlr-4.13.1-complete.jar:$CLASSPATH"
          alias antlr4="sudo java -jar ${pkgs.antlr4}/lib/antlr-4.13.1-complete.jar -o output"
          alias grun="java org.antlr.v4.gui.TestRig"
          JAVA_PATH=$(readlink -f ${pkgs.openjdk17}/bin/java)
          ln -sf "$JAVA_PATH" ~/tools/nix/.paths/java
          echo "ANTLR dev environment is ready!"
        '';
      };
    };
}

