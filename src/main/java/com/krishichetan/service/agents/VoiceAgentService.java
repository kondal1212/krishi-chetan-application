package com.krishichetan.service.agents;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class VoiceAgentService {

    public String transcribeAudio(MultipartFile audioFile) {
        log.info("[VoiceAgent] Transcribing audio...");

        try {
            // MOCK (replace Whisper parsing)
            return "Leaves are turning white and drying";

        } catch (Exception e) {
            log.error("Voice failed", e);
            return "No voice input";
        }
    }
}