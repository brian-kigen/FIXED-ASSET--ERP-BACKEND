package com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Services;

import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Entity.Asset;
import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Entity.AssetRequest;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface AssetService {
    Asset createAsset(Asset asset);
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    void receiveAsset(Long assetId);
    void allocateAsset(Long assetId, String allocatedTo);
    void disposeAsset(Long assetId);
    Integer calculateProfitOrLoss(Long assetId);
    AssetRequest requestAsset(Long assetId, Long userId);
    List<AssetRequest> getAssetRequestsByUserId(Long userId);
    List<AssetRequest> getAllAssetRequests();
    void acceptAssetRequest(Long requestId);
    void rejectAssetRequest(Long requestId);
    List<Asset> getAllAssets();
    List<Asset> getAllUnallocatedAssets();




// Additional methods for asset depreciation calculation, report generation, etc.
}

