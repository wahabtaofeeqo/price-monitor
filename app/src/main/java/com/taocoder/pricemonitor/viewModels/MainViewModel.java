package com.taocoder.pricemonitor.viewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.taocoder.pricemonitor.models.AddressesResult;
import com.taocoder.pricemonitor.models.Approval;
import com.taocoder.pricemonitor.models.ApprovalsResult;
import com.taocoder.pricemonitor.models.Price;
import com.taocoder.pricemonitor.models.PricesResult;
import com.taocoder.pricemonitor.models.ResponseInfo;
import com.taocoder.pricemonitor.models.StationAddress;
import com.taocoder.pricemonitor.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainViewModel extends ViewModel {

    //Competitors price add result
    private MutableLiveData<ResponseInfo<Price>> priceResult = new MutableLiveData<>();

    //Request approval result
    private MutableLiveData<ResponseInfo<Boolean>> approvalResult = new MutableLiveData<>();

    //List of approvals result
    private MutableLiveData<ApprovalsResult> approvals = new MutableLiveData<>();
    private MutableLiveData<ResponseInfo<StationAddress>> addressResult = new MutableLiveData<>();
    private MutableLiveData<AddressesResult> addressesResult = new MutableLiveData<>();
    private MutableLiveData<PricesResult> pricesResult = new MutableLiveData<>();
    private MutableLiveData<List<User>> managers = new MutableLiveData<>();

    //Managers update(Suspend/approve) from HQ result
    MutableLiveData<ResponseInfo<User>> updateResult = new MutableLiveData<>();

    private FirebaseFirestore firestore;

    public MainViewModel() {
        firestore = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<ResponseInfo<Price>> getPriceResult() {
        return priceResult;
    }

    public MutableLiveData<ResponseInfo<Boolean>> getApprovalResult() {
        return approvalResult;
    }

    public MutableLiveData<ApprovalsResult> getApprovals() {
        return approvals;
    }

    public MutableLiveData<ResponseInfo<StationAddress>> getAddressResult() {
        return addressResult;
    }

    public MutableLiveData<AddressesResult> getAddressesResult() {
        return addressesResult;
    }

    public MutableLiveData<PricesResult> getPricesResult() {
        return pricesResult;
    }

    public MutableLiveData<List<User>> getManagers() {
        return managers;
    }

    public MutableLiveData<ResponseInfo<User>> getUpdateResult() {
        return updateResult;
    }

    public void setPrice(Price price) {
        CollectionReference prices = firestore.collection("prices");
        prices.document().set(price).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    priceResult.setValue(new ResponseInfo<Price>(false));
                }
                else {
                    priceResult.setValue(new ResponseInfo<Price>(true, "Price was not added. try again."));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                priceResult.setValue(new ResponseInfo<Price>(true, e.getMessage()));
            }
        });
    }

    public void requestPriceApproval(String price) {
        CollectionReference ref = firestore.collection("approvals");
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Approval approval = new Approval();
        approval.setPrice(Long.parseLong(price));
        approval.setUserId(id);
        approval.setStatus("pending");
        approval.setApproved(false);

        ref.document().set(approval).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    approvalResult.setValue(new ResponseInfo<Boolean>(false));
                }
                else {
                    approvalResult.setValue(new ResponseInfo<Boolean>(true, "Not sent. try again"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                approvalResult.setValue(new ResponseInfo<Boolean>(true, e.getMessage()));
            }
        });
    }

    public void checkPrices() {
        CollectionReference ref = firestore.collection("prices");

        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Price> list = new ArrayList<>();

                if (task.isSuccessful() && task.getResult().size() > 0) {
                    for (DocumentSnapshot snapshot: task.getResult()) {
                        Price price = snapshot.toObject(Price.class);
                        if (price != null) {
                            list.add(price);
                        }
                    }

                    pricesResult.setValue(new PricesResult(false, list));
                }
                else {
                    approvals.setValue(new ApprovalsResult(true, "No Record Found"));
                }
            }
        }) .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pricesResult.setValue(new PricesResult(true, e.getMessage()));
            }
        });
    }

    public void checkApprovals(String type) {
        CollectionReference ref = firestore.collection("approvals");

        ref.whereEqualTo("status", type).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                List<Approval> list = new ArrayList<>();
                if (task.isSuccessful() && task.getResult().size() > 0) {
                    for (DocumentSnapshot snapshot: task.getResult()) {
                        Approval approval = snapshot.toObject(Approval.class);
                        if (approval != null) {
                            list.add(approval);
                        }
                    }

                    approvals.setValue(new ApprovalsResult(false, list));
                }
                else {
                    approvals.setValue(new ApprovalsResult(true, "No Record Found"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                approvals.setValue(new ApprovalsResult(true, e.getMessage()));
            }
        });
    }

    public void addAddress(StationAddress address) {
        CollectionReference ref = firestore.collection("addresses");
        ref.document().set(address).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    addressResult.setValue(new ResponseInfo<StationAddress>(false));
                }
                else {
                    addressResult.setValue(new ResponseInfo<StationAddress>(true, "Address not added. Try again"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addressResult.setValue(new ResponseInfo<StationAddress>(true, e.getMessage()));
            }
        });
    }

    public void addresses() {
        CollectionReference ref = firestore.collection("addresses");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                List<StationAddress> list = new ArrayList<>();

                if (task.isSuccessful()) {
                    for (DocumentSnapshot snapshot: task.getResult()) {
                        StationAddress address = snapshot.toObject(StationAddress.class);
                        if (address != null) {
                            list.add(address);
                        }
                    }
                    addressesResult.setValue(new AddressesResult(false, list));
                }
                else {
                    addressesResult.setValue(new AddressesResult(true, "Addresses not loaded"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addressesResult.setValue(new AddressesResult(true, e.getMessage()));
            }
        });
    }

    public void managers() {

        CollectionReference ref = firestore.collection("users");
        final String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final List<User> list = new ArrayList<>();

        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot snapshot: task.getResult()) {
                        User user = snapshot.toObject(User.class);
                        if (user != null) {
                            user.setDocID(snapshot.getId());
                            if (!user.getId().equalsIgnoreCase(id))
                                list.add(user);
                        }
                    }
                }
                managers.setValue(list);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                managers.setValue(list);
            }
        });
    }

    public void updateUser(final User user) {
        int update = (user.getStatus() == 1) ? 0 : 1;
        user.setStatus(update);

        CollectionReference ref = firestore.collection("users");
        ref.document(user.getDocID()).update("status", update).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateResult.setValue(new ResponseInfo<User>(false, user));
                }
                else {
                    updateResult.setValue(new ResponseInfo<User>(true, "Please try again."));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                updateResult.setValue(new ResponseInfo<User>(true, e.getMessage()));
            }
        });
    }
}
