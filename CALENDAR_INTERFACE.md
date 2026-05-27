# CalendarInteraction

`CalendarInteraction` is the public contract for interacting with the academic calendar component. It provides methods for configuring the semester, managing events, navigating months, handling user selection, and responding to lifecycle events.

All mutating methods return `CalendarInteraction` to allow method chaining.

---

## Listeners

### `OnDayClickListener`

```kotlin
interface OnDayClickListener {
    fun onDayClicked(date: LocalDate, events: List<CalendarEvent>)
}
```

Fired when the user taps a single day cell (tap, not drag).

| Parameter | Description |
|-----------|-------------|
| `date`    | The tapped date |
| `events`  | All events that occur on this date (may be empty) |

Use this to show event details or let the user create a new event on an empty day.

---

### `OnSelectionListener`

```kotlin
interface OnSelectionListener {
    fun onSelectionChanged(start: LocalDate?, end: LocalDate?)
}
```

Fired when the selection changes — either a single-day tap or a drag-to-select range.

| Parameter | Description |
|-----------|-------------|
| `start`   | Start of the selection; `null` if cleared |
| `end`     | End of the range; `null` for single-day selection |

| `start` | `end`   | Meaning |
|---------|---------|---------|
| `D`     | `null`  | Single day selected (tap) |
| `A`     | `B`     | Range selected from A to B (drag) |
| `null`  | `null`  | Selection cleared |

---

### `OnSemesterEndListener`

```kotlin
interface OnSemesterEndListener {
    fun onSemesterEndReached()
}
```

Fired when the current date is past the configured semester end date. Use this to trigger the integration form flow (RF05).

---

## Methods

### `setSemesterRange(start, end)`

```kotlin
fun setSemesterRange(start: LocalDate, end: LocalDate): CalendarInteraction
```

Configures the academic semester boundaries.

| Parameter | Description |
|-----------|-------------|
| `start`   | First day of the semester |
| `end`     | Last day of the semester |

**Behavior:**
- Calendar navigation is restricted to months within `[start, end]`.
- Days outside this range are rendered dimmed and non-interactive.
- The calendar jumps to the semester start month.
- If the current date is past `end`, `OnSemesterEndListener.onSemesterEndReached()` fires immediately.

---

### Event Management

#### `setEvents(eventList)`

```kotlin
fun setEvents(eventList: List<CalendarEvent>): CalendarInteraction
```

Replaces all events with the provided list. Any previously added events are discarded.

| Parameter    | Description |
|--------------|-------------|
| `eventList`  | Full list of events to display |

---

#### `addEvent(event)`

```kotlin
fun addEvent(event: CalendarEvent): CalendarInteraction
```

Appends a single event to the existing event list.

| Parameter | Description |
|-----------|-------------|
| `event`   | The event to add |

---

#### `removeEvent(eventId)`

```kotlin
fun removeEvent(eventId: String): CalendarInteraction
```

Removes the event with the given ID.

| Parameter  | Description |
|------------|-------------|
| `eventId`  | The `id` field of the event to remove |

---

#### `updateEvent(event)`

```kotlin
fun updateEvent(event: CalendarEvent): CalendarInteraction
```

Replaces an existing event with a matching `id`. If no event with that `id` exists, this is a no-op.

| Parameter | Description |
|-----------|-------------|
| `event`   | The updated event (must have the same `id` as the original) |

---

### Event Queries

#### `getEventsOnDate(date)`

```kotlin
fun getEventsOnDate(date: LocalDate): List<CalendarEvent>
```

Returns all events that occur on the given date. An event occurs on a date if the date falls within `[startDate, endDate]` (a single-day event has `endDate == null`, which is treated as `startDate`).

| Parameter | Description |
|-----------|-------------|
| `date`    | The date to query |

**Returns:** possibly-empty list of matching events.

---

#### `getEventsInRange(start, end)`

```kotlin
fun getEventsInRange(start: LocalDate, end: LocalDate): List<CalendarEvent>
```

Returns all events that overlap with the given date range. An event overlaps if its `startDate <= end` and its `endDate >= start` (or `startDate >= start` for single-day events).

| Parameter | Description |
|-----------|-------------|
| `start`   | Range start (inclusive) |
| `end`     | Range end (inclusive) |

**Returns:** possibly-empty list of matching events.

---

### Navigation

#### `goToMonth(year, month)`

```kotlin
fun goToMonth(year: Int, month: Int): CalendarInteraction
```

Navigate to a specific month. `month` is coerced to `1..12`.

| Parameter | Description |
|-----------|-------------|
| `year`    | Calendar year (e.g. 2025) |
| `month`   | Month 1–12 |

---

#### `goToNextMonth()`

```kotlin
fun goToNextMonth(): CalendarInteraction
```

Advance one month forward. Navigation buttons are automatically disabled when the month reaches the semester end.

---

#### `goToPreviousMonth()`

```kotlin
fun goToPreviousMonth(): CalendarInteraction
```

Go back one month. Navigation buttons are automatically disabled when the month reaches the semester start.

---

#### `goToToday()`

```kotlin
fun goToToday(): CalendarInteraction
```

Jump to the current month. If the current date is past the semester end, `OnSemesterEndListener.onSemesterEndReached()` fires.

---

#### `getCurrentYear()` / `getCurrentMonth()`

```kotlin
fun getCurrentYear(): Int
fun getCurrentMonth(): Int
```

Return the year and month (1-based) currently displayed.

---

### Selection

#### `getSelectionStart()` / `getSelectionEnd()`

```kotlin
fun getSelectionStart(): LocalDate?
fun getSelectionEnd(): LocalDate?
```

Return the current selection state.

| Method              | Single-tap | Range-selected | Cleared |
|---------------------|------------|----------------|---------|
| `getSelectionStart` | the date   | range start    | `null`  |
| `getSelectionEnd`   | `null`     | range end      | `null`  |

---

#### `clearSelection()`

```kotlin
fun clearSelection(): CalendarInteraction
```

Deselects any active selection and triggers `OnSelectionListener.onSelectionChanged(null, null)`.

---

### `refresh()`

```kotlin
fun refresh(): CalendarInteraction
```

Re-renders the calendar grid. Call this if you modify events outside the standard CRUD methods and need the view to reflect the changes.

---

## Listener Configuration

### `setOnDayClickListener(listener)`

```kotlin
fun setOnDayClickListener(listener: OnDayClickListener): CalendarInteraction
```

Register a callback for day-cell taps.

---

### `setOnSelectionListener(listener)`

```kotlin
fun setOnSelectionListener(listener: OnSelectionListener): CalendarInteraction
```

Register a callback for selection changes (tap or drag).

---

### `setOnSemesterEndListener(listener)`

```kotlin
fun setOnSemesterEndListener(listener: OnSemesterEndListener): CalendarInteraction
```

Register a callback for when the semester end date is past.

---

## Example: Full Integration

```kotlin
// Layout XML
// <com.example.uniasselvicalendarioapp.view.CalendarComponent
//     android:id="@+id/calendar"
//     android:layout_width="match_parent"
//     android:layout_height="wrap_content"
//     app:todayHighlightColor="@color/blue_500" />

val cal: CalendarInteraction = findViewById(R.id.calendar)

// ── Semester setup ──
val semStart = LocalDate.of(2025, 2, 10)
val semEnd   = LocalDate.of(2025, 7, 5)

cal.setSemesterRange(semStart, semEnd)
   .setEvents(loadEvents())
   .setOnDayClickListener { date, events ->
       if (events.isEmpty()) {
           showCreateEventDialog(date)
       } else {
           showEventDetails(events)
       }
   }
   .setOnSelectionListener { start, end ->
       if (end != null) showRangeOptions(start, end)
   }
   .setOnSemesterEndListener {
       startIntegrationFormActivity()
   }

// ── Create a custom event ──
cal.addEvent(CalendarEvent(
    name = "Entrega TCC",
    description = "Versão final",
    startDate = LocalDate.of(2025, 6, 15),
    startTime = "23:59",
    color = CalendarEvent.EXAM_EVENT_COLOR
))

// ── Remove an event ──
cal.removeEvent(eventId)

// ── Navigate ──
cal.goToToday()
cal.goToMonth(2025, 5)
cal.goToNextMonth()
```

## Data Model: `CalendarEvent`

```kotlin
data class CalendarEvent(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val startTime: String? = null,    // "HH:mm"
    val endTime: String? = null,      // "HH:mm"
    val color: Int = DEFAULT_EVENT_COLOR
)
```

Preset color constants available in `CalendarEvent.Companion`:

| Constant              | Hex         | Usage       |
|-----------------------|-------------|-------------|
| `DEFAULT_EVENT_COLOR` | `#1565C0`   | Generic     |
| `CLASS_EVENT_COLOR`   | `#2E7D32`   | Aula        |
| `EXAM_EVENT_COLOR`    | `#C62828`   | Prova       |
| `HOLIDAY_EVENT_COLOR` | `#F9A825`   | Feriado     |

An event with `endDate == null` is treated as a single-day event on `startDate`.
