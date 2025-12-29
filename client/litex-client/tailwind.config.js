/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        // Twitter/X 主题色
        primary: 'var(--accent-primary, #1d9bf0)',
        'primary-hover': 'var(--accent-hover, #1a8cd8)',
        // 背景色
        'bg-primary': 'var(--bg-primary, #000)',
        'bg-secondary': 'var(--bg-secondary, #16181c)',
        'bg-tertiary': 'var(--bg-tertiary, #202327)',
        'bg-hover': 'var(--bg-hover, #080808)',
        // 文字色
        'text-primary': 'var(--text-primary, #e7e9ea)',
        'text-secondary': 'var(--text-secondary, #71767b)',
        // 边框色
        border: 'var(--border-color, #2f3336)',
        // 状态色
        success: 'var(--success, #00ba7c)',
        error: 'var(--error, #f4212e)',
        warning: 'var(--warning, #ffd400)',
      },
      fontFamily: {
        sans: ['-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Roboto', 'sans-serif'],
      },
      animation: {
        'fade-in': 'fadeIn 0.3s ease-in-out',
        'slide-up': 'slideUp 0.3s ease-out',
        'spin-slow': 'spin 2s linear infinite',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { transform: 'translateY(10px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
      },
      borderRadius: {
        'full': '9999px',
      },
    },
  },
  plugins: [],
}
