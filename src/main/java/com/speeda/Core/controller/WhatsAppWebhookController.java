package com.speeda.Core.controller;

import com.speeda.Core.model.Activity;
import com.speeda.Core.model.AuthToken;
import com.speeda.Core.model.Enum.UserStatus;
import com.speeda.Core.model.Preference;
import com.speeda.Core.model.User;
import com.speeda.Core.repository.ActivityRepository;
import com.speeda.Core.repository.AuthTokenRepository;
import com.speeda.Core.repository.PreferenceRepository;
import com.speeda.Core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WhatsAppWebhookController {
    private static final String VERIFY_TOKEN = "whatsappWebhookToken2024";
    private static final String N8N_WEBHOOK_URL = "https://n8n.speeda.ai/webhook-test/e86f9292-10ec-4025-87f6-e46f9dcd9cce";
    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;
    private final ActivityRepository activityRepository;
    private final PreferenceRepository preferenceRepository;

    @GetMapping("/whatsapp")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.challenge") String challenge,
            @RequestParam("hub.verify_token") String token) {
        return VERIFY_TOKEN.equals(token)
                ? ResponseEntity.ok(challenge)
                : ResponseEntity.status(403).body("Verify token incorrect !");
    }

    @PostMapping("/whatsapp")
    public void receiveWhatsAppEvent(@RequestBody Map<String, Object> payload) {
        try {
            String phoneNumber = extractPhoneNumber(payload);
            String message = extractMessageBody(payload);
            String pdfMediaId = extractPdfMediaId(payload);
            String pdfFilename = extractPdfFilename(payload);
            String voiceMediaId = extractVoiceMediaId(payload);
            String voiceFilename = extractVoiceFilename(payload);
            boolean isText = (message != null);
            boolean isPdf = (pdfMediaId != null && pdfFilename != null);
            boolean isVoice = (voiceMediaId != null);

            if (phoneNumber == null) {
                System.out.println("❌ Numéro de téléphone manquant");
                return;
            }

            boolean tokenValide = false;
            boolean activityExist = false;
            boolean preferenceExist = false;
            boolean userExist = false;

            Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
            if (userOpt.isEmpty()) {
                User newUser = User.builder()
                        .phoneNumber(phoneNumber)
                        .status(UserStatus.INACTIF)
                        .build();
                userRepository.save(newUser);
                System.out.println("👤 Nouvel utilisateur enregistré avec le numéro : " + phoneNumber);
                userOpt = Optional.of(newUser);
            }
            User user = userOpt.get();
            Long userId = user.getId(); // <<<=== ID de l'utilisateur

            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                userExist = true;
            }

            Optional<AuthToken> lastToken = authTokenRepository.findByUser(user).stream()
                    .sorted(Comparator.comparing(AuthToken::getExpiryDate).reversed())
                    .findFirst();
            if (lastToken.isPresent()) {
                tokenValide = lastToken.get().getExpiryDate().isAfter(Instant.now());
            }

            activityExist = activityRepository.findByUser(user).isPresent();
            preferenceExist = preferenceRepository.findByUser(user).isPresent();

            // Logging général
            System.out.println("📞 Numéro            : " + phoneNumber);
            System.out.println("✅ User exist        : " + userExist);
            System.out.println("🔐 Token valide      : " + tokenValide);
            System.out.println("📊 Activité existe   : " + activityExist);
            System.out.println("🎯 Préférence existe : " + preferenceExist);
            System.out.println("🎯 username : " + user.getUsername());

            System.out.println("🏷️ Statut utilisateur : " + user.getStatus().name());
            System.out.println("🆔 User ID           : " + userId);
            System.out.println("📤 Type détecté      : Text=" + isText + " | PDF=" + isPdf + " | Voice=" + isVoice);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> toSend = new HashMap<>();
            toSend.put("user_id", userId); // <<<=== Envoi de l'ID user !
            toSend.put("phone", phoneNumber);
            toSend.put("user_exist", userExist);
            toSend.put("token_valide", tokenValide);
            toSend.put("activity_exist", activityExist);
            toSend.put("preference_exist", preferenceExist);
            toSend.put("status", user.getStatus().name());
            toSend.put("is_text", isText);
            toSend.put("is_pdf", isPdf);
            toSend.put("is_voice", isVoice);
            toSend.put("username", user.getUsername());

            if (isText) {
                toSend.put("message", message);
                System.out.println("📥 Message           : " + message);
            }

            // Cas document PDF
            if (isPdf) {
                toSend.put("pdf_media_id", pdfMediaId);
                toSend.put("pdf_filename", pdfFilename);
                System.out.println("📎 PDF reçu : " + pdfFilename + " (ID: " + pdfMediaId + ")");
            }

            // Cas message vocal
            if (isVoice) {
                toSend.put("voice_media_id", voiceMediaId);
                if (voiceFilename != null) {
                    toSend.put("voice_filename", voiceFilename);
                }
                System.out.println("🎤 Voice reçu : " + voiceFilename + " (ID: " + voiceMediaId + ")");
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(toSend, headers);
            restTemplate.postForEntity(N8N_WEBHOOK_URL, entity, Map.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractPhoneNumber(Map<String, Object> payload) {
        try {
            List<?> entry = (List<?>) payload.get("entry");
            Map<?, ?> entryObj = (Map<?, ?>) entry.get(0);
            List<?> changes = (List<?>) entryObj.get("changes");
            Map<?, ?> changeObj = (Map<?, ?>) changes.get(0);
            Map<?, ?> value = (Map<?, ?>) changeObj.get("value");
            List<?> messages = (List<?>) value.get("messages");
            Map<?, ?> message = (Map<?, ?>) messages.get(0);
            return (String) message.get("from");
        } catch (Exception e) {
            return null;
        }
    }

    private String extractMessageBody(Map<String, Object> payload) {
        try {
            List<?> entry = (List<?>) payload.get("entry");
            Map<?, ?> entryObj = (Map<?, ?>) entry.get(0);
            List<?> changes = (List<?>) entryObj.get("changes");
            Map<?, ?> changeObj = (Map<?, ?>) changes.get(0);
            Map<?, ?> value = (Map<?, ?>) changeObj.get("value");
            List<?> messages = (List<?>) value.get("messages");
            Map<?, ?> message = (Map<?, ?>) messages.get(0);
            if ("text".equals(message.get("type"))) {
                Map<?, ?> text = (Map<?, ?>) message.get("text");
                return (String) text.get("body");
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String extractPdfMediaId(Map<String, Object> payload) {
        try {
            List<?> entry = (List<?>) payload.get("entry");
            Map<?, ?> entryObj = (Map<?, ?>) entry.get(0);
            List<?> changes = (List<?>) entryObj.get("changes");
            Map<?, ?> changeObj = (Map<?, ?>) changes.get(0);
            Map<?, ?> value = (Map<?, ?>) changeObj.get("value");
            List<?> messages = (List<?>) value.get("messages");
            Map<?, ?> message = (Map<?, ?>) messages.get(0);
            if ("document".equals(message.get("type"))) {
                Map<?, ?> document = (Map<?, ?>) message.get("document");
                String mimeType = (String) document.get("mime_type");
                if ("application/pdf".equals(mimeType)) {
                    return (String) document.get("id");
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String extractPdfFilename(Map<String, Object> payload) {
        try {
            List<?> entry = (List<?>) payload.get("entry");
            Map<?, ?> entryObj = (Map<?, ?>) entry.get(0);
            List<?> changes = (List<?>) entryObj.get("changes");
            Map<?, ?> changeObj = (Map<?, ?>) changes.get(0);
            Map<?, ?> value = (Map<?, ?>) changeObj.get("value");
            List<?> messages = (List<?>) value.get("messages");
            Map<?, ?> message = (Map<?, ?>) messages.get(0);
            if ("document".equals(message.get("type"))) {
                Map<?, ?> document = (Map<?, ?>) message.get("document");
                return (String) document.get("filename");
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String extractVoiceMediaId(Map<String, Object> payload) {
        try {
            List<?> entry = (List<?>) payload.get("entry");
            Map<?, ?> entryObj = (Map<?, ?>) entry.get(0);
            List<?> changes = (List<?>) entryObj.get("changes");
            Map<?, ?> changeObj = (Map<?, ?>) changes.get(0);
            Map<?, ?> value = (Map<?, ?>) changeObj.get("value");
            List<?> messages = (List<?>) value.get("messages");
            Map<?, ?> message = (Map<?, ?>) messages.get(0);
            if ("audio".equals(message.get("type"))) {
                Map<?, ?> audio = (Map<?, ?>) message.get("audio");
                Boolean voice = (Boolean) audio.get("voice");
                if (voice != null && voice) {
                    return (String) audio.get("id");
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String extractVoiceFilename(Map<String, Object> payload) {
        try {
            List<?> entry = (List<?>) payload.get("entry");
            Map<?, ?> entryObj = (Map<?, ?>) entry.get(0);
            List<?> changes = (List<?>) entryObj.get("changes");
            Map<?, ?> changeObj = (Map<?, ?>) changes.get(0);
            Map<?, ?> value = (Map<?, ?>) changeObj.get("value");
            List<?> messages = (List<?>) value.get("messages");
            Map<?, ?> message = (Map<?, ?>) messages.get(0);
            if ("audio".equals(message.get("type"))) {
                Map<?, ?> audio = (Map<?, ?>) message.get("audio");
                return (String) audio.get("filename");
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
