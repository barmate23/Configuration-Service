package com.stockmanagementsystem.utils;

import com.stockmanagementsystem.entity.PackagingMaster;
import com.stockmanagementsystem.repository.PackagingMasterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class PackagingMasterCache {

    private final PackagingMasterRepository packagingMasterRepository;

    /**
     * KEY FORMAT:
     * type|subtype|volume(mm3)|diameter(mm)
     */
    private final Map<String, PackagingMaster> cache =
            new ConcurrentHashMap<>();

    public PackagingMasterCache(PackagingMasterRepository packagingMasterRepository) {
        this.packagingMasterRepository = packagingMasterRepository;
    }

    // =====================================================
    // AUTO LOAD ON STARTUP
    // =====================================================
    @PostConstruct
    public void load() {

        log.info("PackagingMasterCache | LOAD START");

        List<PackagingMaster> masters =
                packagingMasterRepository.findAllActiveWithHierarchy();

        for (PackagingMaster pm : masters) {

            String type = pm.getPackagingSubtype().getPackagingType().getTypeName();
            String subtype = pm.getPackagingSubtype().getSubtypeName();

            BigDecimal volumeMm = BigDecimal.ZERO;

            // ✅ Only calculate volume if LWH exists
            if (pm.getLength() != null
                    && pm.getWidth() != null
                    && pm.getHeight() != null) {

                BigDecimal lMm =
                        DimensionKeyUtil.toMillimeter(pm.getLength(), pm.getUom());
                BigDecimal wMm =
                        DimensionKeyUtil.toMillimeter(pm.getWidth(), pm.getUom());
                BigDecimal hMm =
                        DimensionKeyUtil.toMillimeter(pm.getHeight(), pm.getUom());

                volumeMm =
                        DimensionKeyUtil.calculateCanonicalVolume(lMm, wMm, hMm);
            }

            BigDecimal diameterMm = null;
            if (pm.getDiameter() != null) {
                diameterMm = pm.getDiameter(); // assume already canonical
            }

            String key =
                    DimensionKeyUtil.buildKey(
                            type,
                            subtype,
                            volumeMm,
                            diameterMm
                    );

            cache.put(key, pm);
        }

        log.info("PackagingMasterCache | LOAD COMPLETE | cacheSize={}", cache.size());
    }


    // =====================================================
    // LOOKUP
    // =====================================================
    public PackagingMaster find(
            String type,
            String subtype,
            BigDecimal lengthMm,
            BigDecimal widthMm,
            BigDecimal heightMm,
            BigDecimal diameterMm) {

        BigDecimal volume =
                DimensionKeyUtil.calculateCanonicalVolume(
                        lengthMm, widthMm, heightMm);

        String key =
                DimensionKeyUtil.buildKey(
                        type,
                        subtype,
                        volume,
                        diameterMm
                );

        PackagingMaster match = cache.get(key);

        if (match == null) {
            log.warn("PackagingMasterCache | LOOKUP MISS | key={}", key);
        }

        return match;
    }

    // =====================================================
    // DEBUG: SIMILAR KEYS (TYPE + SUBTYPE)
    // =====================================================
    public void logSimilar(String type, String subtype) {

        String typeNorm =
                DimensionKeyUtil.normalizeText(type);
        String subNorm =
                DimensionKeyUtil.normalizeText(subtype);

        log.warn("PackagingMasterCache | SIMILAR KEYS FOR [{}|{}]",
                type, subtype);

        cache.keySet().stream()
                .filter(k -> k.startsWith(typeNorm + "|" + subNorm))
                .forEach(k -> log.warn("CACHE_KEY={}", k));
    }


    public PackagingMaster findByKey(String key) {
        PackagingMaster master = cache.get(key);

        if (master == null) {
            log.warn("PackagingMasterCache | LOOKUP MISS | key={}", key);
        }

        return master;
    }

}
