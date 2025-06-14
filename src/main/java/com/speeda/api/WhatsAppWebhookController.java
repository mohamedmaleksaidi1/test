package com.speeda.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/webhook")
public class WhatsAppWebhookController {

    private static final String VERIFY_TOKEN = "whatsappWebhookToken2024";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenSessionRepository tokenSessionRepository;

    // 1. Vérification du webhook par Meta (GET)
    @GetMapping("/whatsapp")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.challenge") String challenge,
            @RequestParam("hub.verify_token") String verifyToken) {

        if (VERIFY_TOKEN.equals(verifyToken)) {
            return ResponseEntity.ok(challenge);
        } else {
            return ResponseEntity.status(403).body("Verify token incorrect !");
        }
    }

    // 2. Réception des messages WhatsApp (POST)
    @PostMapping("/whatsapp")
    public ResponseEntity<?> receiveWhatsAppEvent(@RequestBody Map<String, Object> payload) {
        try {
            // 1. Extraire le numéro WhatsApp de l'utilisateur (from)
            String phoneNumber = extractPhoneNumberFromPayload(payload);

            if (phoneNumber == null) {
                System.out.println("Aucun numéro trouvé dans le payload !");
                return ResponseEntity.ok().body(Map.of("status", "fail", "message", "Numéro manquant"));
            }

            // 2. Vérifier si l'utilisateur existe
            Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
            if (!userOpt.isPresent()) {
                System.out.println("Utilisateur non trouvé : " + phoneNumber);
                return ResponseEntity.ok().body(Map.of("status", "fail", "message", "user n'existe pas"));
            }

            User user = userOpt.get();

            // 3. (Exemple) Vérifier si le user a un token actif (selon ta logique)
            boolean hasValidToken = user.getTokenSessions().stream()
                    .anyMatch(token -> "ACTIVE".equals(token.getStatus()) && token.getExpiresAt().after(new Date()));

            if (!hasValidToken) {
                System.out.println("Token invalide ou expiré pour user : " + phoneNumber);
                return ResponseEntity.ok().body(Map.of("status", "fail", "message", "token invalide ou expiré"));
            }

            // 4. Succès : user existe et token valide
            System.out.println("Utilisateur authentifié : " + phoneNumber);
            return ResponseEntity.ok(Map.of("status", "success", "message", "user authentifié"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "Erreur serveur"));
        }
    }

    /**
     * Fonction utilitaire pour extraire le numéro WhatsApp du payload JSON Meta/WhatsApp.
     */
    private String extractPhoneNumberFromPayload(Map<String, Object> payload) {
        try {
            List<?> entry = (List<?>) payload.get("entry");
            if (entry == null || entry.isEmpty()) return null;
            Map<?, ?> entryObj = (Map<?, ?>) entry.get(0);

            List<?> changes = (List<?>) entryObj.get("changes");
            if (changes == null || changes.isEmpty()) return null;
            Map<?, ?> changeObj = (Map<?, ?>) changes.get(0);

            Map<?, ?> value = (Map<?, ?>) changeObj.get("value");
            if (value == null) return null;

            List<?> messages = (List<?>) value.get("messages");
            if (messages == null || messages.isEmpty()) return null;
            Map<?, ?> message = (Map<?, ?>) messages.get(0);

            return (String) message.get("from");
        } catch (Exception e) {
            System.err.println("Erreur parsing numéro : " + e.getMessage());
            return null;
        }
    }
}
