import "./index.css";

import logo from "./logo.svg";
import reactLogo from "./react.svg";
import {UpsertCategoryForm} from "@/features/category/components/categoryForm.tsx";

export function App() {
  return (
    <div className="app">
      <div className="logo-container">
        <img src={logo} alt="Bun Logo" className="logo bun-logo" />
        <img src={reactLogo} alt="React Logo" className="logo react-logo" />
      </div>

      <h1>Bun + React</h1>
       <UpsertCategoryForm/>
    </div>
  );
}

export default App;
