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
import com.taocoder.pricemonitor.models.CompetitorPriceAndAddress;
import com.taocoder.pricemonitor.models.PricesResult;
import com.taocoder.pricemonitor.models.ServerResponse;
import com.taocoder.pricemonitor.models.StationAddress;
import com.taocoder.pricemonitor.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainViewModel extends ViewModel {

    //Competitors price add result
    private MutableLiveData<ServerResponse<CompetitorPriceAndAddress>> priceResult = new MutableLiveData<>();

    //Request approval result
    private MutableLiveData<ServerResponse<Boolean>> approvalResult = new MutableLiveData<>();

    //List of approvals result
    private MutableLiveData<ApprovalsResult> approvals = new MutableLiveData<>();

    //Adding address result
    private MutableLiveData<ServerResponse<StationAddress>> addressResult = new MutableLiveData<>();

    //List of addresses result
    private MutableLiveData<AddressesResult> addressesResult = new MutableLiveData<>();

    //Adding competitor address and
    private MutableLiveData<PricesResult> pricesResult = new MutableLiveData<>();

    // List of managers
    private MutableLiveData<List<User>> managers = new MutableLiveData<>();

    //Managers update(Suspend/approve) from HQ result
    MutableLiveData<ServerResponse<User>> updateResult = new MutableLiveData<>();

    //Manager approved price result observable
    MutableLiveData<ServerResponse<Approval>> approvedPrice = new MutableLiveData<>();

    //Approve price result
    MutableLiveData<ServerResponse<Boolean>> approvedResult = new MutableLiveData<>();

    private FirebaseFirestore firestore;

    public MainViewModel() {
        firestore = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<ServerResponse<CompetitorPriceAndAddress>> getPriceResult() {
        return priceResult;
    }

    public MutableLiveData<ServerResponse<Boolean>> getApprovalResult() {
        return approvalResult;
    }

    public MutableLiveData<ApprovalsResult> getApprovals() {
        return approvals;
    }

    public MutableLiveData<ServerResponse<StationAddress>> getAddressResult() {
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

    public MutableLiveData<ServerResponse<User>> getUpdateResult() {
        return updateResult;
    }

    public MutableLiveData<ServerResponse<Approval>> getApprovedPrice() {
        return approvedPrice;
    }

    public MutableLiveData<ServerResponse<Boolean>> getApprovedResult() {
        return approvedResult;
    }

    // Actual action methods

    public void setPrice(CompetitorPriceAndAddress competitorPriceAndAddress) {
        CollectionReference prices = firestore.collection("prices");
        prices.document().set(competitorPriceAndAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    priceResult.setValue(new ServerResponse<CompetitorPriceAndAddress>(false));
                }
                else {
                    priceResult.setValue(new ServerResponse<CompetitorPriceAndAddress>(true, "Price was not added. try again."));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                priceResult.setValue(new ServerResponse<CompetitorPriceAndAddress>(true, e.getMessage()));
            }
        });
    }

    public void requestPriceApproval(String price, String date) {
        CollectionReference ref = firestore.collection("approvals");
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Approval approval = new Approval();

        approval.setPrice(Long.parseLong(price));
        approval.setDate(date);
        approval.setUserId(id);
        approval.setStatus("pending");
        approval.setApproved(false);

        ref.document().set(approval).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    approvalResult.setValue(new ServerResponse<Boolean>(false));
                }
                else {
                    approvalResult.setValue(new ServerResponse<Boolean>(true, "Not sent. try again"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                approvalResult.setValue(new ServerResponse<Boolean>(true, e.getMessage()));
            }
        });
    }

    public void checkPrices() {
        CollectionReference ref = firestore.collection("prices");

        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<CompetitorPriceAndAddress> list = new ArrayList<>();

                if (task.isSuccessful() && task.getResult().size() > 0) {
                    for (DocumentSnapshot snapshot: task.getResult()) {
                        CompetitorPriceAndAddress competitorPriceAndAddress = snapshot.toObject(CompetitorPriceAndAddress.class);
                        if (competitorPriceAndAddress != null) {
                            list.add(competitorPriceAndAddress);
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

    public void approve(String id) {
        CollectionReference ref = firestore.collection("approvals");
        Map<String, Object> map = new HashMap<>();
        map.put("status", "replied");
        map.put("approved", true);

        ref.document(id).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    approvedResult.setValue(new ServerResponse<Boolean>(false));
                }
                else {
                    approvedResult.setValue(new ServerResponse<Boolean>(true, "Not Approved. Try again"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                approvalResult.setValue(new ServerResponse<Boolean>(true, e.getMessage()));
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
                            approval.setId(snapshot.getId());
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
                    addressResult.setValue(new ServerResponse<StationAddress>(false));
                }
                else {
                    addressResult.setValue(new ServerResponse<StationAddress>(true, "Address not added. Try again"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addressResult.setValue(new ServerResponse<StationAddress>(true, e.getMessage()));
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

        ref.whereEqualTo("type", "manager").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    public void updateUser(final User user, String hq) {
        int update = (user.getStatus() == 1) ? 0 : 1;
        user.setStatus(update);

        CollectionReference ref = firestore.collection("users");
        Map<String, Object> map = new HashMap<>();
        map.put("status", update);
        map.put("blockedBy", hq);

        ref.document(user.getDocID()).update("status", update).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateResult.setValue(new ServerResponse<User>(false, user));
                }
                else {
                    updateResult.setValue(new ServerResponse<User>(true, "Please try again."));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                updateResult.setValue(new ServerResponse<User>(true, e.getMessage()));
            }
        });
    }

    public void myApprovedPrice(String id) {

        CollectionReference ref = firestore.collection("approvals");
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Approval approval = null;
                if (task.isSuccessful() && task.getResult().size() > 0) {
                    for (DocumentSnapshot snapshot: task.getResult()) {
                        approval = snapshot.toObject(Approval.class);
                        if (approval != null) break;
                    }
                    approvedPrice.setValue(new ServerResponse<Approval>(false, approval));
                }
                else {
                    approvedPrice.setValue(new ServerResponse<Approval>(true, "Price not Fetched"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
