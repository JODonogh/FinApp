// Example: Using Zustand for State Management
import { create } from 'zustand'
import { devtools } from 'zustand/middleware' // Enables DevTools visualization

// Create a store that can be visualized
const useCounterStore = create(
  devtools(
    (set) => ({
      count: 0,
      increment: () => set((state) => ({ count: state.count + 1 }), false, 'counter/increment'),
      decrement: () => set((state) => ({ count: state.count - 1 }), false, 'counter/decrement'),
    }),
    { name: 'My Data Flow Store' } // Name for the DevTools tab
  )
)

export default useCounterStore