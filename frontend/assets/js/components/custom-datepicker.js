// =====================================================
// CUSTOM DATEPICKER - Calendario Personalizado PNP
// =====================================================

class CustomDatePicker {
    constructor(inputId, options = {}) {
        this.input = document.getElementById(inputId);
        if (!this.input) return;

        this.options = {
            format: 'yyyy-mm-dd',
            locale: 'es-PE',
            ...options
        };

        this.currentDate = new Date();
        this.selectedDate = this.input.value ? new Date(this.input.value) : null;
        this.isOpen = false;

        this.init();
    }

    init() {
        // Crear contenedor del calendario
        this.calendar = document.createElement('div');
        this.calendar.className = 'custom-datepicker';
        this.calendar.style.display = 'none';
        
        // Insertar después del input
        this.input.parentNode.style.position = 'relative';
        this.input.parentNode.insertBefore(this.calendar, this.input.nextSibling);

        // Eventos
        this.input.addEventListener('click', (e) => {
            e.stopPropagation();
            this.toggle();
        });

        document.addEventListener('click', (e) => {
            if (!this.calendar.contains(e.target) && e.target !== this.input) {
                this.close();
            }
        });

        this.render();
    }

    toggle() {
        this.isOpen ? this.close() : this.open();
    }

    open() {
        this.isOpen = true;
        this.calendar.style.display = 'block';
        this.calendar.style.animation = 'fadeIn 0.2s ease';
    }

    close() {
        this.isOpen = false;
        this.calendar.style.display = 'none';
    }

    render() {
        const year = this.currentDate.getFullYear();
        const month = this.currentDate.getMonth();

        const monthNames = [
            'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
            'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
        ];

        const firstDay = new Date(year, month, 1).getDay();
        const daysInMonth = new Date(year, month + 1, 0).getDate();
        const today = new Date();

        let html = `
            <div class="calendar-header">
                <button type="button" class="calendar-nav" data-action="prev-month">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="15 18 9 12 15 6"></polyline>
                    </svg>
                </button>
                <div class="calendar-title">
                    <span class="calendar-month">${monthNames[month]}</span>
                    <span class="calendar-year">${year}</span>
                </div>
                <button type="button" class="calendar-nav" data-action="next-month">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="9 18 15 12 9 6"></polyline>
                    </svg>
                </button>
            </div>
            <div class="calendar-weekdays">
                <div>DO</div><div>LU</div><div>MA</div><div>MI</div>
                <div>JU</div><div>VI</div><div>SA</div>
            </div>
            <div class="calendar-days">
        `;

        // Días vacíos del mes anterior
        for (let i = 0; i < firstDay; i++) {
            html += '<div class="calendar-day empty"></div>';
        }

        // Días del mes actual
        for (let day = 1; day <= daysInMonth; day++) {
            const date = new Date(year, month, day);
            const isToday = date.toDateString() === today.toDateString();
            const isSelected = this.selectedDate && date.toDateString() === this.selectedDate.toDateString();
            
            let classes = 'calendar-day';
            if (isToday) classes += ' today';
            if (isSelected) classes += ' selected';

            html += `<div class="${classes}" data-date="${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}">${day}</div>`;
        }

        html += '</div>';

        // Botones de acción
        html += `
            <div class="calendar-footer">
                <button type="button" class="calendar-btn-today" data-action="today">Hoy</button>
                <button type="button" class="calendar-btn-clear" data-action="clear">Borrar</button>
            </div>
        `;

        this.calendar.innerHTML = html;
        this.attachEventListeners();
    }

    attachEventListeners() {
        // Navegación de meses
        this.calendar.querySelectorAll('.calendar-nav').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
                const action = btn.dataset.action;
                if (action === 'prev-month') {
                    this.currentDate.setMonth(this.currentDate.getMonth() - 1);
                } else {
                    this.currentDate.setMonth(this.currentDate.getMonth() + 1);
                }
                this.render();
            });
        });

        // Selección de día
        this.calendar.querySelectorAll('.calendar-day:not(.empty)').forEach(day => {
            day.addEventListener('click', (e) => {
                e.stopPropagation();
                const dateStr = day.dataset.date;
                this.selectDate(dateStr);
            });
        });

        // Botón "Hoy"
        const todayBtn = this.calendar.querySelector('[data-action="today"]');
        if (todayBtn) {
            todayBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                const today = new Date();
                const dateStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
                this.selectDate(dateStr);
            });
        }

        // Botón "Borrar"
        const clearBtn = this.calendar.querySelector('[data-action="clear"]');
        if (clearBtn) {
            clearBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                this.input.value = '';
                this.selectedDate = null;
                this.close();
            });
        }
    }

    selectDate(dateStr) {
        this.input.value = dateStr;
        this.selectedDate = new Date(dateStr);
        this.close();
        
        // Disparar evento change
        const event = new Event('change', { bubbles: true });
        this.input.dispatchEvent(event);
    }
}

// Auto-inicialización
document.addEventListener('DOMContentLoaded', () => {
    // Inicializar datepickers personalizados
    const dateInputs = document.querySelectorAll('input[type="date"][data-custom-picker="true"]');
    dateInputs.forEach(input => {
        new CustomDatePicker(input.id);
    });
});
