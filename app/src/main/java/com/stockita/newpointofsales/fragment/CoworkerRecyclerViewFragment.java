package com.stockita.newpointofsales.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.stockita.newpointofsales.R;
import com.stockita.newpointofsales.data.ModelCoworkers;
import com.stockita.newpointofsales.utilities.Constant;
import com.stockita.newpointofsales.utilities.Utility;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This fragment is to display all the user co-workers as a friends list
 * in a recycler view, the adapter and the ViewHolder are all in here
 */
public class CoworkerRecyclerViewFragment extends Fragment {

    /**
     * The views
     */
    @Bind(R.id.tab_coworker_list)
    RecyclerView recyclerView;


    /**
     * The owner Encoded Email
     */
    private String mEncodedEmail;


    /**
     * The FirebaseUI adapter
     */
    private FirebaseRecyclerAdapter<ModelCoworkers, CoworkerRecyclerViewFragment.ViewHolder> mAdapter;

    /**
     * Empty constructor
     */
    public CoworkerRecyclerViewFragment() {
    }

    /**
     * Static so we can pass data via {@link android.os.Bundle}
     *
     * @param encodedEmail The owner email
     * @return Fragment
     */
    public static CoworkerRecyclerViewFragment newInstance(String encodedEmail) {

        /* Instantiate this fragment with default constructor */
        CoworkerRecyclerViewFragment fragment = new CoworkerRecyclerViewFragment();

        /* Instantiate the Bundle object */
        Bundle bundle = new Bundle();

        /**
         * Put the data into the bundle
         */
        bundle.putString(Constant.KEY_ENCODED_EMAIL, encodedEmail);

        /* Pass the bundle to the fragment using setArgument() */
        fragment.setArguments(bundle);

        /* Return the fragment */
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Get the encodedEmail from the bundle passed by the activity */
        mEncodedEmail = getArguments().getString(Constant.KEY_ENCODED_EMAIL);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Initialize the view
        View rootView = inflater.inflate(R.layout.fragment_coworker_list, container, false);

        // Initialize the ButterKnife
        ButterKnife.bind(this, rootView);

        /* Firebase co-workers references for this owner */
        Firebase coWorkersRef = new Firebase(Constant.FIREBASE_URL_USER_WORKERS).child(mEncodedEmail);


        /**
         * Setup the recycler view
         */
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        /**
         * Initialize the adapter
         */
        mAdapter =
                new FirebaseRecyclerAdapter<ModelCoworkers, ViewHolder>(ModelCoworkers.class, R.layout.cowroker_adapter, CoworkerRecyclerViewFragment.ViewHolder.class, coWorkersRef) {


                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, ModelCoworkers modelCoworkers, final int position) {

                        /* Get the email address from the Firebase so late we can use it as path */
                        final String childPathEncodedEmail = modelCoworkers.getEmailAddress();

                        /* Get the current booleanStatus so we can change it if the user single on item list */
                        final boolean booleanStatus = modelCoworkers.getBooleanStatus();

                        /* Make the encoded email to decoded email for UI */
                        String decodeEmail = Utility.decodeEmail(childPathEncodedEmail);

                        /* Update the UI */
                        viewHolder.friendsEmail.setText(decodeEmail);
                        viewHolder.friendsName.setText(modelCoworkers.getName());
                        viewHolder.friendsBoolean.setText(String.valueOf(booleanStatus));


                        /**
                         * Set the single click listener to true if it was false.
                         */
                        viewHolder.friendsCard.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                /**
                                 * Get the booleanStatusRef node, pointing to the booleanStatus.
                                 */
                                Firebase booleanStausRef = new Firebase(Constant.FIREBASE_URL_USER_WORKERS)
                                        .child(mEncodedEmail) // The owner
                                        .child(childPathEncodedEmail) // The co-worker
                                        .child(Constant.FIREBASE_PROPERTY_BOOLEAN_STATUS); // The co-worker current boolean status

                                /**
                                 * Check if the current status is true or false, the change vice versa
                                 */
                                if (booleanStatus) {
                                    booleanStausRef.setValue(false);
                                } else {
                                    booleanStausRef.setValue(true);
                                }

                            }
                        });


                        /**
                         * Set the long click listener to delete this node
                         */
                        viewHolder.friendsCard.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                /**
                                 * Get the coworker node, the set the value to null to remove from database.
                                 */
                                Firebase coworkerNode = new Firebase(Constant.FIREBASE_URL_USER_WORKERS)
                                        .child(mEncodedEmail) // The owner
                                        .child(childPathEncodedEmail);

                                /* Before we remove the node, set the boolean status to false */
                                coworkerNode.child(Constant.FIREBASE_PROPERTY_BOOLEAN_STATUS).setValue(false);

                                /* Remove this co-worker from the list */
                                coworkerNode.getParent().setValue(null);

                                /**
                                 * Get the booEmail node, then set the value to null to remove from database
                                 */
                                Firebase bossEmailNode = new Firebase(Constant.FIREBASE_URL_BOSS_EMAIL)
                                        .child(childPathEncodedEmail);

                                /* Remove this co-worker from the node */
                                bossEmailNode.setValue(null);


                                return true;
                            }
                        });
                    }

                    @Override
                    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
                        super.onBindViewHolder(holder, position, payloads);

                        /* Animate the recycler view */
                        Utility.animate(getActivity(), holder, R.anim.anticipateovershoot_interpolator);

                    }

                    /**
                     * Bind the GroupView into the ViewHolder
                     * @return         ViewHolder
                     */
                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                        /* The view group */
                        ViewGroup viewGroup =
                                (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.cowroker_adapter, parent, false);

                        /* Return the view holder and pass the view group as argument */
                        return new CoworkerRecyclerViewFragment.ViewHolder(viewGroup);
                    }
                };


        /**
         * Set the adapter here
         */
        recyclerView.setAdapter(mAdapter);


        // Return the view
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        /* Clean up all the listeners */
        mAdapter.cleanup();
    }





    /**
     * The ViewHolder class extends {@link android.support.v7.widget.RecyclerView.ViewHolder}
     * This is the ViewHolder that will be use by the FirebaseUI adapter
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * The view object binding them
         */
        @Bind(R.id.friends_card)
        LinearLayout friendsCard;
        @Bind(R.id.friends_email)
        TextView friendsEmail;
        @Bind(R.id.friends_name)
        TextView friendsName;
        @Bind(R.id.friends_boolean)
        TextView friendsBoolean;

        /**
         * Constructor
         *
         * @param itemView View object
         */
        public ViewHolder(View itemView) {
            super(itemView);

            /* ButterKnife bind the view */
            ButterKnife.bind(this, itemView);

        }
    }


}
