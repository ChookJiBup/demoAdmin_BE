package com.example.demoadmin.api.festival;

import com.example.demoadmin.api.festival.dto.CreateFestivalRequest;
import com.example.demoadmin.api.festival.dto.CreateFestivalResponse;
import com.example.demoadmin.festival.command.application.FestivalCommandService;
import com.example.demoadmin.global.response.ApiResponse;
import com.example.demoadmin.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 축제 기본 정보 쓰기 API를 제공한다.
 */
@Tag(name = "Festival", description = "축제 기본 정보 API")
@RestController
@RequestMapping("/api/festivals")
@RequiredArgsConstructor
public class FestivalCommandController {

    private final FestivalCommandService festivalCommandService;

    /**
     * 임시 기준의 축제 기본 정보를 저장한다.
     */
    @Operation(summary = "축제 기본 정보 생성")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<CreateFestivalResponse> create(
            @Valid @RequestBody CreateFestivalRequest request
    ) {
        return ApiResponse.success(
                SuccessCode.FESTIVAL_CREATE_SUCCESS,
                CreateFestivalResponse.from(
                        festivalCommandService.create(request.toCommand())
                )
        );
    }
}
