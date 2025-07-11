package com.speeda.Core.controller;

import com.speeda.Core.model.Activity;
import com.speeda.Core.model.AuthToken;
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

            if (phoneNumber == null || message == null) {
                System.out.println("❌ Données manquantes");
                return;
            }

            boolean userExist = false;
            boolean tokenValide = false;
            Map<String, Object> activityData = null;
            Map<String, Object> preferenceData = null;

            Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
            if (userOpt.isPresent()) {
                userExist = true;
                User user = userOpt.get();

                // Vérification du token
                Optional<AuthToken> lastToken = authTokenRepository.findByUser(user).stream()
                        .sorted(Comparator.comparing(AuthToken::getExpiryDate).reversed())
                        .findFirst();
                if (lastToken.isPresent()) {
                    tokenValide = lastToken.get().getExpiryDate().isAfter(Instant.now());
                }

                // Récupération activité
                Optional<Activity> activityOpt = activityRepository.findByUser(user);
                if (activityOpt.isPresent()) {
                    Activity a = activityOpt.get();
                    Map<String, Object> temp = new HashMap<>();
                    if (a.getBusinessName() != null) temp.put("businessName", a.getBusinessName());
                    if (a.getIndustry() != null) temp.put("industry", a.getIndustry());
                    if (a.getBusinessDescription() != null) temp.put("businessDescription", a.getBusinessDescription());
                    if (a.getLocation() != null) temp.put("location", a.getLocation());
                    if (a.getOpeningHours() != null) temp.put("openingHours", a.getOpeningHours());
                    if (a.getAudienceTarget() != null) temp.put("audienceTarget", a.getAudienceTarget());
                    if (a.getBusinessSize() != null) temp.put("businessSize", a.getBusinessSize());
                    if (a.getUniqueSellingPoint() != null) temp.put("uniqueSellingPoint", a.getUniqueSellingPoint());
                    if (a.getYearFounded() != null) temp.put("yearFounded", a.getYearFounded());
                    if (a.getCertifications() != null) temp.put("certifications", a.getCertifications());
                    activityData = temp.isEmpty() ? null : temp;
                }

                // Récupération préférence
                Optional<Preference> prefOpt = preferenceRepository.findByUser(user);
                if (prefOpt.isPresent()) {
                    Preference p = prefOpt.get();
                    Map<String, Object> temp = new HashMap<>();
                    if (p.getToneOfVoice() != null) temp.put("toneOfVoice", p.getToneOfVoice());
                    if (p.getSocialMediaGoals() != null) temp.put("socialMediaGoals", p.getSocialMediaGoals());
                    if (p.getPreferredPlatforms() != null) temp.put("preferredPlatforms", p.getPreferredPlatforms());
                    if (p.getPostingFrequency() != null) temp.put("postingFrequency", p.getPostingFrequency());
                    if (p.getPreferredPostTime() != null) temp.put("preferredPostTime", p.getPreferredPostTime());
                    if (p.getVisualStyle() != null) temp.put("visualStyle", p.getVisualStyle());
                    if (p.getHashtagStrategy() != null) temp.put("hashtagStrategy", p.getHashtagStrategy());
                    if (p.getCompetitorAccounts() != null) temp.put("competitorAccounts", p.getCompetitorAccounts());
                    if (p.getContentTypes() != null) temp.put("contentTypes", p.getContentTypes());
                    if (p.getLanguagePreference() != null) temp.put("languagePreference", p.getLanguagePreference());
                    if (p.getAdditionalNotes() != null) temp.put("additionalNotes", p.getAdditionalNotes());
                    preferenceData = temp.isEmpty() ? null : temp;
                }
            }

            System.out.println("📥 Message       : " + message);
            System.out.println("📞 Numéro        : " + phoneNumber);
            System.out.println("✅ User existe   : " + userExist);
            System.out.println("🔐 Token valide  : " + tokenValide);
            System.out.println("📊 Activité      : " + (activityData != null ? "✅" : "❌"));
            System.out.println("🎯 Préférence    : " + (preferenceData != null ? "✅" : "❌"));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> toSend = new HashMap<>();
            toSend.put("phone", phoneNumber);
            toSend.put("message", message);
            toSend.put("user_exist", userExist);
            toSend.put("token_valide", tokenValide);
            toSend.put("activity", activityData);
            toSend.put("preference", preferenceData);

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
            Map<?, ?> text = (Map<?, ?>) message.get("text");
            return (String) text.get("body");
        } catch (Exception e) {
            return null;
        }
    }
}
