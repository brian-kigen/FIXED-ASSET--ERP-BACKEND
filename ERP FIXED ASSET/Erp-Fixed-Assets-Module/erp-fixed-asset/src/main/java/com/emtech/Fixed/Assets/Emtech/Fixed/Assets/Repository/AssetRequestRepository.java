package com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Repository;

import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Entity.AssetRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetRequestRepository extends JpaRepository<AssetRequest, Long> {
    List<AssetRequest> findByUserId(Long userId);
}
