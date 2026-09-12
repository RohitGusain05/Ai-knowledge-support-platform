import { useEffect, useState } from 'react';

type Space = { id: string; name: string; description?: string };
type Doc = { id: string; filename: string; fileSize: number; processingStatus: string; uploadedAt: string };
type Source = { documentId: string; chunkIndex: number; content: string; similarity: number; filename?: string };
type Answer = { answer: string; sources: Source[] };

const API = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem('token');
  const headers = new Headers(options.headers);
  if (token) headers.set('Authorization', `Bearer ${token}`);
  const response = await fetch(`${API}${path}`, { ...options, headers });
  if (!response.ok) throw new Error(await response.text() || `Request failed (${response.status})`);
  if (response.status === 204) return undefined as T;
  return response.json();
}

export default function App() {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [spaces, setSpaces] = useState<Space[]>([]);
  const [space, setSpace] = useState<Space | null>(null);
  const [docs, setDocs] = useState<Doc[]>([]);
  const [question, setQuestion] = useState('');
  const [answer, setAnswer] = useState<Answer | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showCreateSpace, setShowCreateSpace] = useState(false);
  const [spaceName, setSpaceName] = useState('');
  const [spaceDescription, setSpaceDescription] = useState('');

  const loadSpaces = async () => {
    const data = await request<Space[]>('/api/v1/knowledge-spaces');
    setSpaces(data);
    setSpace(current => current && data.some(item => item.id === current.id) ? current : data[0] ?? null);
  };

  const loadDocs = async (id: string) => setDocs(await request<Doc[]>(`/api/v1/knowledge-spaces/${id}/documents`));

  useEffect(() => {
    if (token) loadSpaces().catch(e => setError(e instanceof Error ? e.message : 'Could not load spaces'));
  }, [token]);

  useEffect(() => {
    if (space) {
      setAnswer(null);
      loadDocs(space.id).catch(e => setError(e instanceof Error ? e.message : 'Could not load documents'));
    } else {
      setDocs([]);
    }
  }, [space]);

  const auth = async () => {
    try {
      setError('');
      const path = mode === 'login' ? '/api/v1/auth/login' : '/api/v1/users';
      const body = mode === 'login' ? { email, password } : { email, password, displayName };
      const result = await request<{ accessToken?: string }>(path, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
      if (mode === 'register') {
        setMode('login');
        setPassword('');
        setError('Account created. Sign in to continue.');
        return;
      }
      if (!result.accessToken) throw new Error('Login did not return an access token.');
      localStorage.setItem('token', result.accessToken);
      setToken(result.accessToken);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Authentication failed');
    }
  };

  const createSpace = async () => {
    if (!spaceName.trim()) return;
    try {
      setError('');
      const created = await request<Space>('/api/v1/knowledge-spaces', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: spaceName.trim(), description: spaceDescription.trim() })
      });
      setSpaces(current => [created, ...current]);
      setSpace(created);
      setSpaceName('');
      setSpaceDescription('');
      setShowCreateSpace(false);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Could not create knowledge space');
    }
  };

  const upload = async (file: File) => {
    if (!space) return;
    try {
      setError('');
      const form = new FormData();
      form.append('file', file);
      await request(`/api/v1/knowledge-spaces/${space.id}/documents`, { method: 'POST', body: form });
      await loadDocs(space.id);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Upload failed');
    }
  };

  const ask = async () => {
    if (!space || !question.trim() || loading) return;
    try {
      setLoading(true);
      setError('');
      setAnswer(null);
      const result = await request<Answer>(`/api/v1/knowledge-spaces/${space.id}/questions`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ question: question.trim(), limit: 5 })
      });
      setAnswer(result);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Question failed');
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    setToken(null);
    setSpaces([]);
    setSpace(null);
    setAnswer(null);
  };

  if (!token) {
    return (
      <main className="auth">
        <section className="auth-card">
          <div className="brand">KNOW<span>LEDGE</span></div>
          <h1>{mode === 'login' ? 'Welcome back.' : 'Create your account.'}</h1>
          <p className="muted">Private document intelligence, grounded in your own knowledge.</p>
          {mode === 'register' && <input placeholder="Display name" value={displayName} onChange={e => setDisplayName(e.target.value)} />}
          <input placeholder="Email" type="email" value={email} onChange={e => setEmail(e.target.value)} />
          <input placeholder="Password" type="password" value={password} onChange={e => setPassword(e.target.value)} />
          <button onClick={auth}>{mode === 'login' ? 'Sign in' : 'Create account'}</button>
          {error && <p className={error.startsWith('Account created') ? 'success' : 'error'}>{error}</p>}
          <button className="link" onClick={() => { setError(''); setMode(mode === 'login' ? 'register' : 'login'); }}>
            {mode === 'login' ? 'Create an account' : 'Back to sign in'}
          </button>
        </section>
      </main>
    );
  }

  return (
    <main className="app">
      <header>
        <div className="brand">KNOW<span>LEDGE</span></div>
        <button className="logout" onClick={logout}>Log out</button>
      </header>

      <div className="layout">
        <aside>
          <div className="sidebar-heading">
            <h3>Knowledge spaces</h3>
            <button className="icon-button" onClick={() => setShowCreateSpace(true)} aria-label="Create knowledge space">+</button>
          </div>
          {spaces.map(s => (
            <button className={space?.id === s.id ? 'space active' : 'space'} key={s.id} onClick={() => setSpace(s)}>{s.name}</button>
          ))}
          {!spaces.length && <p className="muted">Create your first space to get started.</p>}
        </aside>

        <section className="content">
          <div className="hero">
            <p className="eyebrow">AI KNOWLEDGE SUPPORT</p>
            <h1>{space?.name ?? 'Your private knowledge base'}</h1>
            <p className="muted">Upload internal knowledge, then ask questions and receive answers grounded in your documents.</p>
          </div>

          {!space && (
            <div className="empty-state">
              <h2>No knowledge space selected</h2>
              <p className="muted">Create a space, upload your documents, and start asking questions.</p>
              <button onClick={() => setShowCreateSpace(true)}>Create knowledge space</button>
            </div>
          )}

          {space && <>
            <div className="upload">
              <label>Upload PDF, TXT or Markdown
                <input type="file" accept=".pdf,.txt,.md,.markdown" onChange={e => e.target.files?.[0] && upload(e.target.files[0])} />
              </label>
            </div>

            <div className="docs">
              <h2>Documents</h2>
              {docs.length ? docs.map(d => (
                <div className="doc" key={d.id}>
                  <span>{d.filename}</span>
                  <small>{d.processingStatus} · {(d.fileSize / 1024).toFixed(0)} KB</small>
                </div>
              )) : <p className="muted">No documents yet. Upload a PDF, TXT, or Markdown file.</p>}
            </div>

            <div className="chat">
              <h2>Ask your knowledge</h2>
              <div className="ask">
                <input placeholder="What does the documentation say about..." value={question} onChange={e => setQuestion(e.target.value)} onKeyDown={e => e.key === 'Enter' && ask()} />
                <button onClick={ask} disabled={loading}>{loading ? 'Thinking…' : 'Ask'}</button>
              </div>
              {answer && <article className="answer">
                <p>{answer.answer}</p>
                <h4>Sources</h4>
                {answer.sources.map((s, i) => (
                  <details key={`${s.documentId}-${s.chunkIndex}`}>
                    <summary>Source {i + 1}{s.filename ? ` · ${s.filename}` : ''} · {(s.similarity * 100).toFixed(1)}% match</summary>
                    <p>{s.content}</p>
                  </details>
                ))}
              </article>}
            </div>
          </>}
          {error && <p className="error">{error}</p>}
        </section>
      </div>

      {showCreateSpace && <div className="modal-backdrop" onClick={() => setShowCreateSpace(false)}>
        <section className="modal" onClick={e => e.stopPropagation()}>
          <h2>Create knowledge space</h2>
          <input placeholder="Space name" value={spaceName} onChange={e => setSpaceName(e.target.value)} autoFocus />
          <textarea placeholder="Description (optional)" value={spaceDescription} onChange={e => setSpaceDescription(e.target.value)} rows={4} />
          <div className="modal-actions">
            <button className="secondary" onClick={() => setShowCreateSpace(false)}>Cancel</button>
            <button onClick={createSpace} disabled={!spaceName.trim()}>Create</button>
          </div>
        </section>
      </div>}
    </main>
  );
}
