package com.controle_comercial.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilitário para gerar hash BCrypt de senhas.
 *
 * Uso via linha de comando:
 * mvn exec:java -Dexec.mainClass="com.controle_comercial.util.PasswordHashGenerator" -Dexec.args="minhaSenha"
 *
 * Ou compile e rode direto:
 * javac PasswordHashGenerator.java
 * java PasswordHashGenerator minhaSenha
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("========================================");
            System.out.println("   Gerador de Hash BCrypt para Senhas");
            System.out.println("========================================");
            System.out.println("\nUso: java PasswordHashGenerator <senha>");
            System.out.println("\nExemplo:");
            System.out.println("  mvn exec:java -Dexec.mainClass=\"com.controle_comercial.util.PasswordHashGenerator\" -Dexec.args=\"admin123\"");
            System.out.println("\n⚠️  ATENÇÃO: Use este utilitário apenas para gerar hashes de produção de forma segura.");
            System.out.println("             Nunca logue senhas ou hashes em arquivos de log da aplicação!");
            return;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String senha = args[0];
        String hash = encoder.encode(senha);

        System.out.println("\n========================================");
        System.out.println("   Hash BCrypt Gerado");
        System.out.println("========================================");
        System.out.println("\nSenha: " + senha);
        System.out.println("\nHash BCrypt:");
        System.out.println(hash);
        System.out.println("\n========================================");
        System.out.println("   SQL para Atualizar Banco");
        System.out.println("========================================");
        System.out.println("\n-- Exemplo de UPDATE:");
        System.out.println("UPDATE Usuario SET senha = '" + hash + "' WHERE usuario = 'admin';");
        System.out.println("\n-- Exemplo de INSERT:");
        System.out.println("INSERT INTO Usuario (usuario, senha, status) VALUES ('admin', '" + hash + "', 'ADMIN');");
        System.out.println("\n========================================\n");
    }
}
