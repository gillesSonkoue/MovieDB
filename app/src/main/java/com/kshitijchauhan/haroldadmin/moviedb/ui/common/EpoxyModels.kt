package com.kshitijchauhan.haroldadmin.moviedb.ui.common

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.kshitijchauhan.haroldadmin.moviedb.R
import org.koin.core.parameter.parametersOf
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

@EpoxyModelClass(layout = R.layout.item_section_header)
abstract class HeaderModel : EpoxyModelWithHolder<HeaderModel.HeaderViewHolder>() {

    @EpoxyAttribute
    lateinit var title: String

    @EpoxyAttribute
    lateinit var transitionName: String

    override fun bind(holder: HeaderViewHolder) {
        super.bind(holder)
        holder.title.text = title
    }

    class HeaderViewHolder : KotlinEpoxyHolder() {
        val title by bind<TextView>(R.id.tvSectionHeader)
    }
}

@EpoxyModelClass(layout = R.layout.item_moviegrid)
abstract class MovieModel : EpoxyModelWithHolder<MovieModel.MovieViewHolder>() {

    @EpoxyAttribute
    lateinit var posterUrl: String

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var clickListener: View.OnClickListener

    @EpoxyAttribute
    lateinit var transitionName: String

    @EpoxyAttribute
    var movieId: Int? = null

    override fun bind(holder: MovieViewHolder) {
        super.bind(holder)
        holder.glide
            .load(posterUrl)
            .into(holder.poster)
        ViewCompat.setTransitionName(holder.poster, transitionName)
        holder.poster.setOnClickListener(clickListener)
    }

    inner class MovieViewHolder : KotlinEpoxyHolder(), KoinComponent {
        val poster by bind<ImageView>(R.id.ivPoster)
        val glide by inject<RequestManager>("view-glide-request-manager") {
            parametersOf(poster)
        }
    }
}

@EpoxyModelClass(layout = R.layout.view_info_text)
abstract class InfoTextModel : EpoxyModelWithHolder<InfoTextModel.InfoTextViewHolder>() {

    @EpoxyAttribute
    lateinit var text: String

    override fun bind(holder: InfoTextViewHolder) {
        super.bind(holder)
        holder.textView.text = text
    }

    inner class InfoTextViewHolder : KotlinEpoxyHolder() {
        val textView by bind<TextView>(R.id.tvInfoText)
    }
}

@EpoxyModelClass(layout = R.layout.view_need_to_login)
abstract class NeedToLoginModel : EpoxyModelWithHolder<NeedToLoginModel.NeedToLoginViewHolder>() {

    inner class NeedToLoginViewHolder : KotlinEpoxyHolder() {
        val textview by bind<TextView>(R.id.tvNeedToLogin)
    }
}

@EpoxyModelClass(layout = R.layout.item_credit_actor)
abstract class ActorModel: EpoxyModelWithHolder<ActorModel.ActorHolder>() {

    @EpoxyAttribute
    var actorId: Int? = null

    @EpoxyAttribute
    lateinit var name: String

    @EpoxyAttribute
    lateinit var pictureUrl: String

    @EpoxyAttribute
    lateinit var transitionName: String

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var clickListener: View.OnClickListener

    override fun bind(holder: ActorHolder) {
        super.bind(holder)
        with(holder) {
            actorName.text = name
            glide
                .load(pictureUrl)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.ic_round_account_circle_24px)
                        .error(R.drawable.ic_round_account_circle_24px)
                        .fallback(R.drawable.ic_round_account_circle_24px)
                )
                .into(actorPicture)
            actorPicture.setOnClickListener(clickListener)
            ViewCompat.setTransitionName(actorPicture, transitionName)
        }
    }

    inner class ActorHolder: KotlinEpoxyHolder(), KoinComponent {
        val actorName by bind<TextView>(R.id.tvCreditActorName)
        val actorPicture by bind <ImageView>(R.id.ivCreditActorPhoto)
        val glide by inject<RequestManager>("view-glide-request-manager") {
            parametersOf(actorPicture)
        }
    }
}