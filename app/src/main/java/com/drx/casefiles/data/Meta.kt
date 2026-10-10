package com.drx.casefiles.data

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.drx.casefiles.R as AppR

enum class Category(@StringRes val label: Int) {
    CLINICAL(AppR.string.cat_clinical),
    XRAY(AppR.string.cat_xray),
    MRI(AppR.string.cat_mri),
    CT(AppR.string.cat_ct),
    ECHO(AppR.string.cat_echo),
    LAB(AppR.string.cat_lab),
    REPORT(AppR.string.cat_report)
}

enum class Side(@StringRes val label: Int) {
    NONE(AppR.string.side_none),
    L(AppR.string.side_left),
    R(AppR.string.side_right)
}

enum class Region(@StringRes val label: Int) {
    SHOULDER(AppR.string.r_shoulder), ARM(AppR.string.r_arm), ELBOW(AppR.string.r_elbow),
    FOREARM(AppR.string.r_forearm), WRIST(AppR.string.r_wrist), HAND(AppR.string.r_hand),
    PELVIS(AppR.string.r_pelvis), HIP(AppR.string.r_hip), THIGH(AppR.string.r_thigh),
    KNEE(AppR.string.r_knee), LEG(AppR.string.r_leg), ANKLE(AppR.string.r_ankle),
    FOOT(AppR.string.r_foot), SPINE(AppR.string.r_spine), OTHER(AppR.string.r_other)
}

enum class Stage(@StringRes val label: Int) {
    BEFORE_REDUCTION(AppR.string.st_before_reduction),
    AFTER_REDUCTION(AppR.string.st_after_reduction),
    AFTER_CAST(AppR.string.st_after_cast),
    PRE_OP(AppR.string.st_pre_op),
    POST_OP(AppR.string.st_post_op),
    CONTROL_FIXATION(AppR.string.st_control_fixation)
}

enum class ViewPos(@StringRes val label: Int) {
    AP(AppR.string.v_ap), LATERAL(AppR.string.v_lateral), OBLIQUE(AppR.string.v_oblique),
    PA(AppR.string.v_pa), MORTISE(AppR.string.v_mortise), GRASHEY(AppR.string.v_grashey),
    YVIEW(AppR.string.v_yview), AXILLARY(AppR.string.v_axillary), ROBERTS(AppR.string.v_roberts),
    SKYLINE(AppR.string.v_skyline), ANTERIOR(AppR.string.v_anterior), POSTERIOR(AppR.string.v_posterior),
    MEDIAL(AppR.string.v_medial), AXIAL(AppR.string.v_axial), STRESS(AppR.string.v_stress)
}

inline fun <reified T : Enum<T>> enumOrNull(name: String): T? =
    enumValues<T>().firstOrNull { it.name == name }

fun sideShort(side: String): String = when (side) {
    "L" -> "L"
    "R" -> "R"
    else -> ""
}

@Composable
fun stageText(stage: String): String {
    val s = enumOrNull<Stage>(stage)
    return when {
        s != null -> stringResource(s.label)
        stage.isBlank() -> stringResource(AppR.string.stage_none)
        else -> stage
    }
}

@Composable
fun regionText(region: String): String {
    val r = enumOrNull<Region>(region)
    return if (r != null) stringResource(r.label) else ""
}

@Composable
fun viewText(viewPos: String): String {
    val v = enumOrNull<ViewPos>(viewPos)
    return if (v != null) stringResource(v.label) else ""
}

@Composable
fun categoryText(category: String): String {
    val c = enumOrNull<Category>(category)
    return if (c != null) stringResource(c.label) else category
}

/** Label for every visit, e.g. "Follow-up 2". Follow-ups are numbered per patient by date. */
fun visitLabels(ctx: Context, visits: List<Visit>): Map<Long, String> {
    val out = HashMap<Long, String>()
    visits.groupBy { it.patientId }.values.forEach { list ->
        var n = 0
        list.sortedBy { it.date }.forEach { v ->
            out[v.id] = when (v.kind) {
                "INITIAL" -> ctx.getString(AppR.string.visit_initial)
                "FOLLOWUP" -> { n++; ctx.getString(AppR.string.visit_followup_fmt, n) }
                else -> v.title.ifBlank { ctx.getString(AppR.string.visit_followup) }
            }
        }
    }
    return out
}

/** Days since the first visit of the same patient. */
fun dayNumber(visit: Visit, patientVisits: List<Visit>): Int {
    val start = patientVisits.firstOrNull { it.kind == "INITIAL" }?.date
        ?: patientVisits.minOfOrNull { it.date } ?: visit.date
    return ((visit.date - start) / 86_400_000L).toInt().coerceAtLeast(0)
}
