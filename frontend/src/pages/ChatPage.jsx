import { useEffect, useRef, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client/dist/sockjs';
import toast from 'react-hot-toast';
import { apiRequest, getWebSocketUrl } from '../api/client';
import { useAuth } from '../context/AuthContext';
import Navbar from '../components/Navbar';
import { timeAgo } from '../utils/format';

export default function ChatPage() {
  const { user } = useAuth();
  const location = useLocation();
  const [partners, setPartners] = useState([]);
  const [active, setActive] = useState(null);
  const [messages, setMessages] = useState([]);
  const [text, setText] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [searching, setSearching] = useState(false);
  const bottomRef = useRef(null);

  const loadPartners = () => {
    apiRequest('/api/chat/partners').then((r) => setPartners(r.data || [])).catch(() => {});
  };

  useEffect(() => {
    loadPartners();
    apiRequest('/api/chat/online?online=true', { method: 'POST' }).catch(() => {});
    return () => {
      apiRequest('/api/chat/online?online=false', { method: 'POST' }).catch(() => {});
    };
  }, []);

  useEffect(() => {
    if (location.state?.userId) {
      setActive({
        userId: location.state.userId,
        name: location.state.name || 'User',
        profileImageUrl: location.state.profileImageUrl,
        online: false,
      });
    }
  }, [location.state]);

  useEffect(() => {
    if (!user?.userId) return;
    const client = new Client({
      webSocketFactory: () => new SockJS(getWebSocketUrl()),
      reconnectDelay: 5000,
      onConnect: () => {
        client.subscribe(`/topic/chat/${user.userId}`, (msg) => {
          const m = JSON.parse(msg.body);
          setMessages((prev) => {
            if (!active) return prev;
            if (m.senderId === active.userId || m.receiverId === active.userId) {
              return [...prev, m];
            }
            return prev;
          });
          loadPartners();
        });
      },
    });
    client.activate();
    return () => client.deactivate();
  }, [user?.userId, active?.userId]);

  useEffect(() => {
    if (!active) return;
    apiRequest(`/api/chat/conversation/${active.userId}`)
      .then((r) => setMessages(r.data || []))
      .catch(() => toast.error('Could not load messages'));
  }, [active?.userId]);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  useEffect(() => {
    if (!searchQuery.trim()) {
      setSearchResults([]);
      return;
    }
    const timer = setTimeout(async () => {
      setSearching(true);
      try {
        const res = await apiRequest(`/api/chat/users/search?q=${encodeURIComponent(searchQuery.trim())}`);
        setSearchResults(res.data || []);
      } catch {
        setSearchResults([]);
      } finally {
        setSearching(false);
      }
    }, 300);
    return () => clearTimeout(timer);
  }, [searchQuery]);

  const send = async (e) => {
    e.preventDefault();
    if (!text.trim() || !active) return;
    try {
      const res = await apiRequest('/api/chat/send', {
        method: 'POST',
        body: JSON.stringify({ receiverId: active.userId, content: text }),
      });
      setMessages((prev) => [...prev, res.data]);
      setText('');
      loadPartners();
    } catch (err) {
      toast.error(err.message);
    }
  };

  const selectUser = (u) => {
    setActive(u);
    setSearchQuery('');
    setSearchResults([]);
  };

  const displayList = searchQuery.trim() ? searchResults : partners;

  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      <div className="max-w-6xl mx-auto px-4 py-8 h-[calc(100vh-8rem)]">
        <div className="card h-full flex overflow-hidden p-0">
          <aside className="w-80 border-r border-white/5 flex flex-col shrink-0">
            <p className="p-4 font-poppins font-semibold border-b border-white/5">Messages</p>
            <div className="p-3 border-b border-white/5">
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search users by name or email..."
                className="input-field text-sm"
              />
              {searching && <p className="text-xs text-muted mt-2">Searching...</p>}
            </div>
            <div className="flex-1 overflow-y-auto">
              {displayList.map((p) => (
                <button
                  key={p.userId}
                  type="button"
                  onClick={() => selectUser(p)}
                  className={`w-full text-left p-4 flex gap-3 hover:bg-white/5 ${active?.userId === p.userId ? 'bg-accent/10' : ''}`}
                >
                  <div className="relative">
                    <img
                      src={p.profileImageUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(p.name)}`}
                      alt=""
                      className="w-10 h-10 rounded-full"
                    />
                    {p.online && (
                      <span className="absolute bottom-0 right-0 w-2.5 h-2.5 bg-green-500 rounded-full border-2 border-surface" />
                    )}
                  </div>
                  <div className="min-w-0">
                    <p className="font-medium text-sm truncate">{p.name}</p>
                    <p className="text-xs text-muted truncate">
                      {searchQuery.trim() ? 'Start chat' : p.lastMessage || 'No messages yet'}
                    </p>
                  </div>
                </button>
              ))}
              {displayList.length === 0 && (
                <p className="p-4 text-sm text-muted">
                  {searchQuery.trim() ? 'No users found' : 'No conversations yet. Search to find someone.'}
                </p>
              )}
            </div>
          </aside>
          <main className="flex-1 flex flex-col min-w-0">
            {active ? (
              <>
                <div className="p-4 border-b border-white/5 flex items-center gap-3">
                  <img
                    src={active.profileImageUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(active.name)}`}
                    alt=""
                    className="w-10 h-10 rounded-full"
                  />
                  <div>
                    <p className="font-semibold">{active.name}</p>
                    <p className="text-xs text-muted">{active.online ? 'Online' : 'Offline'}</p>
                  </div>
                </div>
                <div className="flex-1 overflow-y-auto p-4 space-y-3">
                  {messages.map((m) => (
                    <div key={m.id} className={`flex ${m.senderId === user.userId ? 'justify-end' : 'justify-start'}`}>
                      <div
                        className={`max-w-[70%] px-4 py-2 rounded-2xl text-sm ${
                          m.senderId === user.userId ? 'bg-accent text-background' : 'bg-white/10'
                        }`}
                      >
                        <p>{m.content}</p>
                        <p
                          className={`text-[10px] mt-1 ${
                            m.senderId === user.userId ? 'text-background/70' : 'text-muted'
                          }`}
                        >
                          {timeAgo(m.sentAt)}
                        </p>
                      </div>
                      </div>
                  ))}
                  <div ref={bottomRef} />
                </div>
                <form onSubmit={send} className="p-4 border-t border-white/5 flex gap-2">
                  <input
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                    placeholder="Type a message..."
                    className="input-field flex-1"
                  />
                  <button type="submit" className="btn-primary">Send</button>
                </form>
              </>
            ) : (
              <div className="flex-1 flex items-center justify-center text-muted text-center px-4">
                Select a conversation or search for a user to message
              </div>
            )}
          </main>
        </div>
      </div>
    </div>
  );
}
