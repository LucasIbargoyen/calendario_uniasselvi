package com.example.uniasselvicalendarioapp.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.uniasselvicalendarioapp.R
import com.example.uniasselvicalendarioapp.data.CalendarEvent
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class CalendarComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), CalendarInteraction {

    private lateinit var tvMonthYear: TextView
    private lateinit var btnPrev: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var dayGrid: GridLayout

    private var currentYearMonth: YearMonth = YearMonth.now()
    private var semesterStart: LocalDate? = null
    private var semesterEnd: LocalDate? = null
    private val events = mutableListOf<CalendarEvent>()
    private var onDayClickListener: OnDayClickListener? = null
    private var onSelectionListener: OnSelectionListener? = null
    private var onSemesterEndListener: OnSemesterEndListener? = null

    private var selStart: LocalDate? = null
    private var selEnd: LocalDate? = null
    private var todayAccent: Int = 0
    private var offset = 0
    private val TOTAL_CELLS = 35  // 5 rows x 7 cols, always the same

    // cell grid metrics (recalculated on each touch)
    private var cellW = 1; private var cellH = 1; private var rowsN = 1

    // touch state
    private var touchDate: LocalDate? = null
    private var startTouch: LocalDate? = null
    private var touchIdx = -1
    private var isDragging = false
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop * 2

    // range overlay paint
    private val rangePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x3342A5F5.toInt()
        style = Paint.Style.FILL
    }

    private val dateMap = HashMap<View, LocalDate>()

    /**
     * Single grid-level touch listener. No long press — drag starts
     * immediately when the finger moves past touchSlop.
     *
     *   Tap   (down+up, no significant move) → single selection
     *   Drag  (down+move past threshold)    → range selection
     */
    private val gridListener = View.OnTouchListener { _, ev ->
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                touchIdx = cellAt(ev.x, ev.y)
                val cell = if (touchIdx >= 0) dayGrid.getChildAt(touchIdx) else null
                if (cell != null && cell.isEnabled) {
                    touchDate = dateMap[cell]
                    startTouch = touchDate
                    isDragging = false
                    cell.alpha = 0.5f
                    true
                } else { touchIdx = -1; touchDate = null; false }
            }
            MotionEvent.ACTION_MOVE -> {
                if (touchDate == null) return@OnTouchListener true
                val dx = ev.x - (dayGrid.getChildAt(touchIdx)?.left?.toFloat() ?: ev.x)
                val dy = ev.y - (dayGrid.getChildAt(touchIdx)?.top?.toFloat() ?: ev.y)
                val dist = Math.hypot(dx.toDouble(), dy.toDouble()).toFloat()

                if (!isDragging && dist > touchSlop) {
                    // ── start drag ──
                    isDragging = true
                    selStart = touchDate; selEnd = touchDate
                    onSelectionListener?.onSelectionChanged(selStart, selEnd)
                    renderCalendar()
                }

                if (isDragging) {
                    val idx = cellAt(ev.x, ev.y)
                    if (idx >= 0 && idx != touchIdx) {
                        dayGrid.getChildAt(touchIdx)?.alpha = 1f
                        val cur = dayGrid.getChildAt(idx)
                        if (cur != null && cur.isEnabled) {
                            cur.alpha = 0.5f; touchIdx = idx
                            val d = dateMap[cur] ?: return@OnTouchListener true
                            extendTo(d); renderCalendar()
                        }
                    }
                }
                true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                dayGrid.getChildAt(touchIdx)?.alpha = 1f
                if (isDragging) {
                    isDragging = false
                } else if (touchDate != null) {
                    tap(touchDate!!)
                }
                touchDate = null; startTouch = null; touchIdx = -1
                true
            }
            else -> false
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_calendar_component, this, true)
        tvMonthYear = findViewById(R.id.tv_month_year)
        btnPrev = findViewById(R.id.btn_prev_month)
        btnNext = findViewById(R.id.btn_next_month)
        dayGrid = findViewById(R.id.day_grid)

        val a = context.obtainStyledAttributes(attrs, R.styleable.CalendarComponent)
        todayAccent = a.getColor(R.styleable.CalendarComponent_todayHighlightColor,
            ContextCompat.getColor(context, R.color.today_default))
        a.recycle()

        dayGrid.setOnTouchListener(gridListener)

        if (isInEditMode) renderEditorPreview()
        else {
            btnPrev.setOnClickListener { goToPreviousMonth() }
            btnNext.setOnClickListener { goToNextMonth() }
            renderCalendar()
        }
    }

    // ─── Public API ───────────────────────────────────────────────────────

    override fun setSemesterRange(start: LocalDate, end: LocalDate): CalendarComponent {
        semesterStart = start; semesterEnd = end
        currentYearMonth = YearMonth.from(start)
        renderCalendar(); checkSemesterEnd(); return this
    }

    private fun checkSemesterEnd() {
        val e = semesterEnd ?: return
        if (LocalDate.now().isAfter(e)) onSemesterEndListener?.onSemesterEndReached()
    }

    override fun setEvents(eventList: List<CalendarEvent>): CalendarComponent {
        events.clear(); events.addAll(eventList); renderCalendar(); return this
    }

    override fun addEvent(event: CalendarEvent): CalendarComponent {
        events.add(event); renderCalendar(); return this
    }

    override fun removeEvent(eventId: String): CalendarComponent {
        events.removeAll { it.id == eventId }; renderCalendar(); return this
    }

    override fun updateEvent(event: CalendarEvent): CalendarComponent {
        val i = events.indexOfFirst { it.id == event.id }
        if (i >= 0) events[i] = event; renderCalendar(); return this
    }

    override fun getEventsOnDate(date: LocalDate): List<CalendarEvent> =
        events.filter { it.occursOn(date) }

    override fun getEventsInRange(start: LocalDate, end: LocalDate): List<CalendarEvent> =
        events.filter { e -> !e.startDate.isAfter(end) && (e.endDate == null || !e.endDate!!.isBefore(start)) }

    override fun setOnSemesterEndListener(listener: OnSemesterEndListener): CalendarInteraction {
        onSemesterEndListener = listener; return this
    }

    override fun goToMonth(year: Int, month: Int): CalendarComponent {
        currentYearMonth = YearMonth.of(year, month.coerceIn(1, 12))
        renderCalendar(); return this
    }

    override fun goToNextMonth(): CalendarComponent {
        currentYearMonth = currentYearMonth.plusMonths(1); renderCalendar(); return this
    }

    override fun goToPreviousMonth(): CalendarComponent {
        currentYearMonth = currentYearMonth.minusMonths(1); renderCalendar(); return this
    }

    override fun goToToday(): CalendarComponent {
        currentYearMonth = YearMonth.now(); renderCalendar(); checkSemesterEnd(); return this
    }

    override fun getCurrentYear(): Int = currentYearMonth.year
    override fun getCurrentMonth(): Int = currentYearMonth.monthValue
    override fun getSelectionStart(): LocalDate? = selStart
    override fun getSelectionEnd(): LocalDate? = selEnd

    override fun setOnDayClickListener(listener: OnDayClickListener): CalendarInteraction {
        onDayClickListener = listener; return this
    }

    override fun setOnSelectionListener(listener: OnSelectionListener): CalendarInteraction {
        onSelectionListener = listener; return this
    }

    override fun clearSelection(): CalendarComponent {
        selStart = null; selEnd = null
        onSelectionListener?.onSelectionChanged(null, null)
        renderCalendar(); return this
    }

    override fun refresh(): CalendarComponent {
        renderCalendar(); return this
    }

    // ─── Editor Preview ──────────────────────────────────────────────────

    private fun renderEditorPreview() {
        tvMonthYear.text = "2026 \u2022 Maio"
        dayGrid.removeAllViews(); dateMap.clear()
        val o = YearMonth.of(2026, 5).atDay(1).dayOfWeek.value.let { if (it == 7) 0 else it }
        for (i in 0 until o) dayGrid.addView(previewEmptyCell())
        for (day in 1..31) dayGrid.addView(previewDayCell(day, day == 15, day == 5 || day == 20))
    }

    private fun previewEmptyCell(): View {
        val v = View(context); v.layoutParams = gridParams(); v.minimumHeight = dp(40f); return v
    }

    private fun previewDayCell(day: Int, isToday: Boolean, hasDot: Boolean): View {
        val c = LinearLayout(context); c.orientation = LinearLayout.VERTICAL; c.gravity = Gravity.CENTER
        c.layoutParams = gridParams(); c.minimumHeight = dp(40f)
        val tv = TextView(context); tv.text = day.toString(); tv.gravity = Gravity.CENTER; tv.textSize = 14f
        if (isToday) { tv.setBackgroundResource(R.drawable.bg_calendar_today); tv.setTextColor(-0x1) }
        else { tv.setTextColor(-0x1000000) }
        c.addView(tv, ViewGroup.LayoutParams(dp(40f), dp(40f)))
        if (hasDot) { val s = dp(5f); val d = View(context); d.layoutParams = ViewGroup.LayoutParams(s, s)
            d.background = oval(s, 0xFF1565C0.toInt()); c.addView(d) }
        return c
    }

    // ─── Calendar Rendering ──────────────────────────────────────────────

    private fun renderCalendar() {
        tvMonthYear.text = formatMonthYear(currentYearMonth)
        updateNavigation()
        dayGrid.removeAllViews(); dateMap.clear()

        val ym = currentYearMonth
        offset = ym.atDay(1).dayOfWeek.value.let { if (it == 7) 0 else it }
        val prev = ym.minusMonths(1)
        val next = ym.plusMonths(1)
        val daysInMonth = ym.lengthOfMonth()
        val daysInPrev = prev.lengthOfMonth()
        val today = LocalDate.now()
        val dotSize = resources.getDimensionPixelSize(R.dimen.event_dot_size)

        // ── filler days from previous month ──
        for (i in 0 until offset) {
            val date = prev.atDay(daysInPrev - offset + 1 + i)
            dayGrid.addView(shadowCell(date))
        }

        // ── current month days ──
        for (day in 1..daysInMonth) {
            dayGrid.addView(dayCell(ym.atDay(day), today, dotSize))
        }

        // ── filler days from next month ──
        val remaining = TOTAL_CELLS - offset - daysInMonth
        for (i in 0 until remaining) {
            dayGrid.addView(shadowCell(next.atDay(1 + i)))
        }
    }

    private fun updateNavigation() {
        btnPrev.isEnabled = semesterStart == null || currentYearMonth.isAfter(YearMonth.from(semesterStart))
        btnNext.isEnabled = semesterEnd == null || currentYearMonth.isBefore(YearMonth.from(semesterEnd))
    }

    private fun inflateCell() = LayoutInflater.from(context).inflate(R.layout.view_calendar_day_cell, dayGrid, false)

    private fun shadowCell(date: LocalDate): View {
        val cell = inflateCell()
        cell.layoutParams = gridParams(); cell.isEnabled = false
        cell.alpha = 0.75f
        val tv = cell.findViewById<TextView>(R.id.tv_day_number)
        tv.text = date.dayOfMonth.toString(); tv.alpha = 0.5f
        tv.setTextColor(-0x1000000)
        dateMap[cell] = date
        return cell
    }

    private fun dayCell(date: LocalDate, today: LocalDate, dotSize: Int): View {
        val cell = inflateCell()
        val root = cell.findViewById<FrameLayout>(R.id.cell_root)
        val tv = cell.findViewById<TextView>(R.id.tv_day_number)
        tv.text = date.dayOfMonth.toString()
        cell.layoutParams = gridParams()

        val month = YearMonth.from(date)
        val past = semesterStart != null && month.isBefore(YearMonth.from(semesterStart))
        val future = semesterEnd != null && month.isAfter(YearMonth.from(semesterEnd))

        if (past || future) {
            tv.alpha = if (past) 0.25f else 0.35f
            cell.alpha = if (past) 0.3f else 0.5f; cell.isEnabled = false
            dateMap[cell] = date; return cell
        }

        cell.isEnabled = true; cell.alpha = 1f; tv.alpha = 1f
        dateMap[cell] = date

        val evs = this.events.filter { it.occursOn(date) }
        paint(cell, root, tv, date, date == today, evs)

        val dots = cell.findViewById<LinearLayout>(R.id.event_dots_container); dots.removeAllViews()
        val max = 4; val vis = if (evs.size > max) evs.take(max - 1) else evs
        vis.forEach { dots.addView(eventDot(dotSize, it.color)) }
        if (evs.size > max) dots.addView(moreDot(dotSize))

        cell.contentDescription = desc(date, evs)
        return cell
    }

    // ─── Painting ────────────────────────────────────────────────────────

    private fun paint(
        cell: View, root: FrameLayout, tv: TextView, date: LocalDate,
        isToday: Boolean, evs: List<CalendarEvent>
    ) {
        val s = selStart; val sz = dp(36f)
        cell.setBackgroundColor(0) // overlay handles range fill

        if (s == null) { paintDef(tv, isToday); return }

        val e = selEnd
        // single selection
        if (e == null || s == e) {
            if (date == s) { tv.background = oval(sz, todayAccent); tv.setTextColor(-0x1) }
            else { paintDef(tv, isToday) }
            return
        }

        // range: only first and last cells get the filled oval
        val first = s; val last = e
        if (date == first || date == last) {
            tv.background = oval(sz, todayAccent); tv.setTextColor(-0x1)
        } else if (!date.isBefore(first) && !date.isAfter(last)) {
            paintDef(tv, isToday)
        } else {
            paintDef(tv, isToday)
        }
    }

    private fun paintDef(tv: TextView, isToday: Boolean) {
        if (isToday) { tv.setBackgroundResource(R.drawable.bg_calendar_today); tv.setTextColor(-0x1) }
        else { tv.background = null; tv.setTextColor(-0x1000000) }
    }

    private fun oval(s: Int, c: Int) = GradientDrawable().apply {
        shape = GradientDrawable.OVAL; setSize(s, s); setColor(c)
    }

    /** Returns the child index at grid-local (x, y), or -1 */
    private fun cellAt(x: Float, y: Float): Int {
        recalcMetrics()
        val col = (x / cellW).toInt().coerceIn(0, 6)
        val row = (y / cellH).toInt().coerceIn(0, rowsN - 1)
        val idx = row * 7 + col
        return if (idx in 0 until dayGrid.childCount) idx else -1
    }

    private fun recalcMetrics() {
        val gw = dayGrid.width - dayGrid.paddingLeft - dayGrid.paddingRight
        if (gw > 0) cellW = gw / 7
        var maxB = 0; var r = 0
        for (i in 0 until dayGrid.childCount) {
            val ch = dayGrid.getChildAt(i)
            if (ch.bottom > maxB) { maxB = ch.bottom; r++ }
        }
        if (r > 0) { rowsN = r; cellH = if (maxB > 0) maxB / r else cellH }
    }

    private fun extendTo(date: LocalDate) {
        val s = selStart ?: return
        if (date.isBefore(s)) { selStart = date; selEnd = s }
        else { selStart = s; selEnd = date }
        onSelectionListener?.onSelectionChanged(selStart, selEnd)
    }

    private fun tap(date: LocalDate) {
        val evs = events.filter { it.occursOn(date) }
        onDayClickListener?.onDayClicked(date, evs)
        selStart = date; selEnd = null
        onSelectionListener?.onSelectionChanged(selStart, selEnd)
        renderCalendar()
    }

    // ─── Range Overlay ──────────────────────────────────────────────────
    // Draws a single rounded rectangle per row spanning the selected cells,
    // rendered below the cell content but above the grid background.

    override fun dispatchDraw(canvas: Canvas) {
        val s = selStart; val e = selEnd
        if (s != null && e != null && s != e) drawRangeOverlay(canvas)
        super.dispatchDraw(canvas)
    }

    private fun drawRangeOverlay(canvas: Canvas) {
        val grid = dayGrid; val n = grid.childCount
        if (n == 0) return
        val s = selStart!!; val e = selEnd!!

        // grid position relative to this FrameLayout
        var ox = grid.left; var oy = grid.top
        var p: ViewParent = grid.parent
        while (p is View && p !== this) { ox += p.left - p.scrollX; oy += p.top - p.scrollY; p = p.parent }

        val rad = dp(18f).toFloat()
        canvas.save(); canvas.translate(ox.toFloat(), oy.toFloat())

        val cols = 7; val rows = (n + cols - 1) / cols
        for (row in 0 until rows) {
            var first: View? = null; var last: View? = null
            for (col in 0 until cols) {
                val idx = row * cols + col
                if (idx >= n) break
                val cell = grid.getChildAt(idx)
                val d = dateMap[cell] ?: continue
                if (!d.isBefore(s) && !d.isAfter(e)) {
                    if (first == null) first = cell; last = cell
                }
            }
            if (first != null && last != null) {
                canvas.drawRoundRect(
                    first.left.toFloat(), first.top.toFloat(),
                    last.right.toFloat(), last.bottom.toFloat(), rad, rad, rangePaint
                )
            }
        }
        canvas.restore()
    }

    // ─── Helpers ─────────────────────────────────────────────────────────

    private fun gridParams() = GridLayout.LayoutParams(
        GridLayout.spec(GridLayout.UNDEFINED),
        GridLayout.spec(GridLayout.UNDEFINED, 1f)
    ).apply { width = 0; height = GridLayout.LayoutParams.WRAP_CONTENT }

    private fun eventDot(s: Int, c: Int) = View(context).apply {
        layoutParams = ViewGroup.MarginLayoutParams(s, s).apply { setMargins(dp(1f), 0, dp(1f), 0) }
        background = GradientDrawable().apply { shape = GradientDrawable.OVAL; setSize(s, s); setColor(c) }
    }

    private fun moreDot(s: Int) = View(context).apply {
        layoutParams = ViewGroup.MarginLayoutParams(s, s).apply { setMargins(dp(1f), 0, dp(1f), 0) }
        background = GradientDrawable().apply { shape = GradientDrawable.OVAL; setSize(s, s); setColor(0xFF9E9E9E.toInt()) }
    }

    private fun formatMonthYear(ym: YearMonth): String {
        val m = ym.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pt-BR"))
        return "${ym.year} \u2022 ${m.replaceFirstChar { it.uppercase() }}"
    }

    private fun desc(date: LocalDate, evs: List<CalendarEvent>): String {
        val dow = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pt-BR"))
        val mon = date.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pt-BR"))
        val b = "$dow, ${date.dayOfMonth} de $mon de ${date.year}"
        return if (evs.isEmpty()) b else "$b. Eventos: ${evs.joinToString("; ") { it.name }}"
    }

    private fun dp(f: Float): Int = (f * resources.displayMetrics.density).toInt()
}
