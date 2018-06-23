package ru.jufy.myposh.ui.settings

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jufy.mgtshr.ui.base.BaseFragment
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.BranchError
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import io.branch.referral.util.ShareSheetStyle
import ru.jufy.myposh.R
import ru.jufy.myposh.presentation.settings.SettingsMvpView
import ru.jufy.myposh.presentation.settings.SettingsPresenter
import javax.inject.Inject


class SettingsFragment : BaseFragment(), SettingsMvpView {

    @Inject
    lateinit var presenter:SettingsPresenter<SettingsMvpView>

    internal lateinit var rootView: View
    internal lateinit var recyclerView: RecyclerView
    internal lateinit var adapter: SettingsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_settings, container, false)
        presenter.onAttach(this)
        setUp(rootView)

        return rootView
    }

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

    override fun setUp(view: View?) {
        recyclerView = rootView.findViewById(R.id.recycler)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = SettingsAdapter(activity, presenter)
        recyclerView.adapter = adapter
    }

    override fun shareReferralCode(code: String) {
        val branchUniversalObject = BranchUniversalObject()
                .setCanonicalIdentifier("referral")
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(ContentMetadata().addCustomMetadata("referral_code", code))

        val lp = LinkProperties()
                .addControlParameter("referral_code", code)
                .setFeature("sharing")



        val ss = ShareSheetStyle(context!!, "Install this awesome app", "Install this awesome app ")
                .setAsFullWidthStyle(true)
                .setSharingTitle("Share With")

        branchUniversalObject.showShareSheet(activity!!, lp, ss, object : Branch.BranchLinkShareListener {
            override fun onShareLinkDialogLaunched() {}
            override fun onShareLinkDialogDismissed() {}
            override fun onLinkShareResponse(sharedLink: String, sharedChannel: String, error: BranchError) {}
            override fun onChannelSelected(channelName: String) {}
        })
    }


}
