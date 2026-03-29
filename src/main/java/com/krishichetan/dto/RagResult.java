package com.krishichetan.dto;

import java.util.List;

public record RagResult(
        String solution,
        List<String> steps,
        String source
) {}
