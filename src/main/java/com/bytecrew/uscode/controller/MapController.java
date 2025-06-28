package com.bytecrew.uscode.controller;

import com.bytecrew.uscode.dto.Coordinates;
import com.bytecrew.uscode.dto.RentalLocation;
import com.bytecrew.uscode.service.MapApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
@Tag(name = "Map API", description = "주소 → 좌표 변환 및 가장 가까운 농기구 대여지점 검색 API")
public class MapController {

    private final MapApiService mapApiService;

    @GetMapping("/coordinates")
    @Operation(
            summary = "주소를 위도/경도로 변환",
            description = "입력한 도로명 주소 또는 지번 주소를 카카오 지도 API를 이용해 위도(latitude)와 경도(longitude)로 변환합니다."
    )
    public Coordinates getCoordinates(
            @Parameter(description = "검색할 주소 (도로명 또는 지번)", example = "경북 의성군 금성면 탑운길 115")
            @RequestParam String address
    ) {
        return mapApiService.getCoordinates(address);
    }

    @GetMapping("/nearest")
    @Operation(
            summary = "가장 가까운 농기구 대여지점 검색",
            description = """
            입력한 주소를 기반으로 가장 가까운 농기구 대여 장소를 반환합니다. <br/>
            거리 계산은 위도/경도를 기반으로 하며 Haversine 공식을 사용합니다.
        """
    )
    public RentalLocation getNearestRentalLocation(
            @Parameter(description = "검색 기준이 되는 주소", example = "경북 의성군 봉양면 경북대로 5225")
            @RequestParam String address
    ) {
        Coordinates target = mapApiService.getCoordinates(address);

        List<RentalLocation> rentalLocations = List.of(
                new RentalLocation("경북 의성군 금성면 탑운길 115", "금성지소", "054-830-6483", 36.2623, 128.6871),
                new RentalLocation("경북 의성군 봉양면 경북대로 5225", "의성군농업기술센터 본소", "054-830-6751", 36.323, 128.6461),
                new RentalLocation("경북 의성군 안계면 서부로 1800", "안계지소", "054-830-6486", 36.3929, 128.4318)
        );

        return rentalLocations.stream()
                .min(Comparator.comparingDouble(loc -> haversine(
                        target.latitude(), target.longitude(),
                        loc.latitude(), loc.longitude()
                )))
                .orElseThrow(() -> new RuntimeException("가장 가까운 지점을 찾을 수 없습니다."));
    }

    // Haversine 거리 계산 (단위: km)
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // 지구 반지름
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
